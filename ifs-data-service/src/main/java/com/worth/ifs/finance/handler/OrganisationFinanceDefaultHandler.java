package com.worth.ifs.finance.handler;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.ApplicationFinanceRow;
import com.worth.ifs.finance.domain.FinanceRowMetaField;
import com.worth.ifs.finance.handler.item.*;
import com.worth.ifs.finance.repository.ApplicationFinanceRowRepository;
import com.worth.ifs.finance.repository.FinanceRowMetaFieldRepository;
import com.worth.ifs.finance.resource.category.*;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * OrganisationFinanceDefaultHandler maintains the finances from
 * an organisation's perspective and calculates the totals
 */
@Component
public class OrganisationFinanceDefaultHandler implements OrganisationFinanceHandler {
    private static final Log LOG = LogFactory.getLog(OrganisationFinanceDefaultHandler.class);

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ApplicationFinanceRowRepository financeRowRepository;

    @Autowired
    private FinanceRowMetaFieldRepository financeRowMetaFieldRepository;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Override
    public Iterable<ApplicationFinanceRow> initialiseCostType(ApplicationFinance applicationFinance, FinanceRowType costType){
    	
    	if(costTypeSupportedByHandler(costType)) {
	        Question question = getQuestionByCostType(costType);
	        try{
	            List<ApplicationFinanceRow> cost = getCostHandler(costType).initializeCost();
	            cost.forEach(c -> {
	                c.setQuestion(question);
	                c.setTarget(applicationFinance);
	            });
	            if(!cost.isEmpty()){
	                financeRowRepository.save(cost);
	                return cost;
	            }else{
	                return new ArrayList<>();
	            }
	
	        }catch (IllegalArgumentException e){
	            LOG.error(String.format("No FinanceRowHandler for type: %s", costType.getType()), e);
	        }
    	}
        return null;
    }

	// TODO DW - INFUND-1555 - handle rest result
    private Question getQuestionByCostType(FinanceRowType costType) {
        return questionService.getQuestionByFormInputType(costType.getType()).getSuccessObjectOrThrowException();
    }

    @Override
    public Map<FinanceRowType, FinanceRowCostCategory> getOrganisationFinances(Long applicationFinanceId) {
    	Map<FinanceRowType, FinanceRowCostCategory> costCategories = createCostCategories();
        List<ApplicationFinanceRow> costs = financeRowRepository.findByTargetId(applicationFinanceId);
        costCategories = addCostsToCategories(costCategories, costs);
        costCategories = calculateTotals(costCategories);
        return costCategories;
    }

    @Override
    public Map<FinanceRowType, FinanceRowCostCategory> getOrganisationFinanceTotals(Long applicationFinanceId, Competition competition) {
    	Map<FinanceRowType, FinanceRowCostCategory> costCategories = getOrganisationFinances(applicationFinanceId);
        return resetCosts(costCategories);
    }

    private Map<FinanceRowType, FinanceRowCostCategory> createCostCategories() {
    	Map<FinanceRowType, FinanceRowCostCategory> costCategories = new EnumMap<>(FinanceRowType.class);
        for(FinanceRowType costType : FinanceRowType.values()) {
            FinanceRowCostCategory financeRowCostCategory = createCostCategoryByType(costType);
            costCategories.put(costType, financeRowCostCategory);
        }
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> addCostsToCategories(Map<FinanceRowType, FinanceRowCostCategory> costCategories, List<ApplicationFinanceRow> costs) {
        costs.stream().forEach(c -> addCostToCategory(costCategories, c));
        return costCategories;
    }

    /**
     * The costs are converted to a representation based on its type that can be used in the view and
     * are added to a specific Category (e.g. labour).
     * @param cost ApplicationFinanceRow to be added
     */
    private Map<FinanceRowType, FinanceRowCostCategory> addCostToCategory(Map<FinanceRowType, FinanceRowCostCategory> costCategories, ApplicationFinanceRow cost) {
        FinanceRowType costType = FinanceRowType.fromString(cost.getQuestion().getFormInputs().get(0).getFormInputType().getTitle());
        FinanceRowHandler financeRowHandler = getCostHandler(costType);
        FinanceRowItem costItem = financeRowHandler.toCostItem(cost);
        FinanceRowCostCategory financeRowCostCategory = costCategories.get(costType);
        financeRowCostCategory.addCost(costItem);
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> calculateTotals(Map<FinanceRowType, FinanceRowCostCategory> costCategories) {
        costCategories.values()
                .forEach(cc -> cc.calculateTotal());
        return calculateOverheadTotal(costCategories);
    }

    private Map<FinanceRowType, FinanceRowCostCategory> calculateOverheadTotal(Map<FinanceRowType, FinanceRowCostCategory> costCategories) {
        FinanceRowCostCategory labourFinanceRowCostCategory = costCategories.get(FinanceRowType.LABOUR);
        OverheadCostCategory overheadCategory = (OverheadCostCategory) costCategories.get(FinanceRowType.OVERHEADS);
        overheadCategory.setLabourCostTotal(labourFinanceRowCostCategory.getTotal());
        overheadCategory.calculateTotal();
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> resetCosts(Map<FinanceRowType, FinanceRowCostCategory> costCategories) {
        costCategories.values()
                .forEach(cc -> cc.setCosts(new ArrayList<>()));
        return costCategories;
    }

    @Override
    public ApplicationFinanceRow costItemToCost(FinanceRowItem costItem) {
        FinanceRowHandler financeRowHandler = getCostHandler(costItem.getCostType());
        List<FinanceRowMetaField> financeRowMetaFields = financeRowMetaFieldRepository.findAll();
        financeRowHandler.setCostFields(financeRowMetaFields);
        return financeRowHandler.toCost(costItem);
    }

    @Override
    public FinanceRowItem costToCostItem(ApplicationFinanceRow cost) {
        FinanceRowType costType = FinanceRowType.fromString(cost.getQuestion().getFormInputs().get(0).getFormInputType().getTitle());
        FinanceRowHandler financeRowHandler = getCostHandler(costType);
        return financeRowHandler.toCostItem(cost);
    }

    @Override
    public List<FinanceRowItem> costToCostItem(List<ApplicationFinanceRow> costs) {
        List<FinanceRowItem> costItems = new ArrayList<>();
        costs.stream()
                .forEach(cost -> costItems.add(costToCostItem(cost)));
        return costItems;
    }

    @Override
    public List<ApplicationFinanceRow> costItemsToCost(List<FinanceRowItem> costItems) {
        List<ApplicationFinanceRow> costs = new ArrayList<>();
        costItems.stream()
                .forEach(costItem -> costs.add(costItemToCost(costItem)));
        return costs;
    }

    private boolean costTypeSupportedByHandler(FinanceRowType costType) {
		return !(FinanceRowType.YOUR_FINANCE.equals(costType) || FinanceRowType.ACADEMIC.equals(costType));
	}

    @Override
    public FinanceRowHandler getCostHandler(FinanceRowType costType) {
        FinanceRowHandler handler = null;
        switch(costType) {
            case LABOUR:
                handler = new LabourCostHandler();
                break;
            case CAPITAL_USAGE:
                handler = new CapitalUsageHandler();
                break;
            case MATERIALS:
                handler = new MaterialsHandler();
                break;
            case OTHER_COSTS:
                handler = new OtherCostHandler();
                break;
            case OVERHEADS:
                handler = new OverheadsHandler();
                break;
            case SUBCONTRACTING_COSTS:
                handler = new SubContractingCostHandler();
                break;
            case TRAVEL:
                handler = new TravelCostHandler();
                break;
            case FINANCE:
                handler = new GrantClaimHandler();
                break;
            case OTHER_FUNDING:
                handler = new OtherFundingHandler();
                break;
        }
        if(handler != null){
            // some times the handler needs autowired classes
            beanFactory.autowireBean(handler);
            return handler;
        }
        LOG.error("Not a valid FinanceType: " + costType);
        throw new IllegalArgumentException("Not a valid FinanceType: " + costType);
    }

    private FinanceRowCostCategory createCostCategoryByType(FinanceRowType costType) {
        switch(costType) {
            case LABOUR:
                return new LabourCostCategory();
            case OTHER_FUNDING:
                return new OtherFundingCostCategory();
            case OVERHEADS:
                return new OverheadCostCategory();
            case FINANCE:
                return new GrantClaimCategory();
            default:
                return new DefaultCostCategory();
        }
    }

}
