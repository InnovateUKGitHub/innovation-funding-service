package com.worth.ifs.finance.handler;

import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.FinanceRow;
import com.worth.ifs.finance.domain.FinanceRowMetaField;
import com.worth.ifs.finance.handler.item.FinanceRowHandler;
import com.worth.ifs.finance.handler.item.JESCostHandler;
import com.worth.ifs.finance.repository.FinanceRowMetaFieldRepository;
import com.worth.ifs.finance.repository.FinanceRowRepository;
import com.worth.ifs.finance.resource.category.FinanceRowCostCategory;
import com.worth.ifs.finance.resource.category.DefaultCostCategory;
import com.worth.ifs.finance.resource.category.GrantClaimCategory;
import com.worth.ifs.finance.resource.cost.AcademicCost;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import com.worth.ifs.finance.resource.cost.GrantClaim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrganisationJESFinance implements OrganisationFinanceHandler {
    
    @Autowired
    private FinanceRowRepository financeRowRepository;

    @Autowired
    private FinanceRowMetaFieldRepository financeRowMetaFieldRepository;

    @Override
    public Iterable<FinanceRow> initialiseCostType(ApplicationFinance applicationFinance, FinanceRowType costType) {
        return null;
    }

    @Override
    public Map<FinanceRowType, FinanceRowCostCategory> getOrganisationFinances(Long applicationFinanceId) {
        List<FinanceRow> costs = financeRowRepository.findByApplicationFinanceId(applicationFinanceId);
        Map<FinanceRowType, FinanceRowCostCategory> costCategories = createCostCategories();
        costCategories = addCostsToCategories(costCategories, costs);
        costCategories = calculateTotals(costCategories);
        return costCategories;
    }

    @Override
    public Map<FinanceRowType, FinanceRowCostCategory> getOrganisationFinanceTotals(Long applicationFinanceId, Competition competition) {
    	Map<FinanceRowType, FinanceRowCostCategory> costCategories = getOrganisationFinances(applicationFinanceId);
    	costCategories = setGrantClaimPercentage(costCategories, competition);
    	costCategories = calculateTotals(costCategories);
        return resetCosts(costCategories);
    }

    private Map<FinanceRowType, FinanceRowCostCategory> setGrantClaimPercentage(Map<FinanceRowType, FinanceRowCostCategory> costCategories, Competition competition) {
        FinanceRowItem costItem = new GrantClaim(0L, competition.getAcademicGrantPercentage());
        costCategories.get(FinanceRowType.FINANCE).addCost(costItem);
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> calculateTotals(Map<FinanceRowType, FinanceRowCostCategory> costCategories) {
        costCategories.values()
                .forEach(cc -> cc.calculateTotal());
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> resetCosts(Map<FinanceRowType, FinanceRowCostCategory> costCategories) {
        costCategories.values()
                .forEach(cc -> cc.setCosts(new ArrayList<>()));
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> createCostCategories() {
    	Map<FinanceRowType, FinanceRowCostCategory> costCategories = new EnumMap<>(FinanceRowType.class);

        for(FinanceRowType costType : FinanceRowType.values()) {
            FinanceRowCostCategory financeRowCostCategory;
            switch (costType) {
                case FINANCE:
                    financeRowCostCategory = new GrantClaimCategory();
                    break;
                default:
                    financeRowCostCategory = new DefaultCostCategory();

            }
            costCategories.put(costType, financeRowCostCategory);
        }
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> addCostsToCategories(Map<FinanceRowType, FinanceRowCostCategory> costCategories, List<FinanceRow> costs) {
        costs.stream().forEach(c -> addCostToCategory(costCategories, c));
        return costCategories;
    }

    private void addCostToCategory(Map<FinanceRowType, FinanceRowCostCategory> costCategories, FinanceRow cost) {
        FinanceRowType costType = FinanceRowType.fromString(cost.getQuestion().getFormInputs().get(0).getFormInputType().getTitle());
        FinanceRowItem costItem = toCostItem(cost);
        FinanceRowCostCategory financeRowCostCategory = costCategories.get(costType);
        financeRowCostCategory.addCost(costItem);
    }

    private FinanceRowItem toCostItem(FinanceRow cost) {
        return new AcademicCost(cost.getId(), cost.getName(), cost.getCost(), cost.getItem());
    }

    @Override
    public FinanceRow costItemToCost(FinanceRowItem costItem) {
        FinanceRowHandler financeRowHandler = new JESCostHandler();
        List<FinanceRowMetaField> financeRowMetaFields = financeRowMetaFieldRepository.findAll();
        financeRowHandler.setCostFields(financeRowMetaFields);
        return financeRowHandler.toCost(costItem);
    }

    @Override
    public FinanceRowItem costToCostItem(FinanceRow cost) {
        FinanceRowHandler financeRowHandler = new JESCostHandler();
        return financeRowHandler.toCostItem(cost);
    }

    @Override
    public FinanceRowHandler getCostHandler(FinanceRowType costType) {
        return new JESCostHandler();
    }

    @Override
    public List<FinanceRowItem> costToCostItem(List<FinanceRow> costs) {
        return costs.stream().map(c -> costToCostItem(c)).collect(Collectors.toList());
    }

    @Override
    public List<FinanceRow> costItemsToCost(List<FinanceRowItem> costItems) {
        return costItems.stream().map(c -> costItemToCost(c)).collect(Collectors.toList());
    }
}
