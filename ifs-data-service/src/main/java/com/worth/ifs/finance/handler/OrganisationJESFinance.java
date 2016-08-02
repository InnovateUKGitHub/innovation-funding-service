package com.worth.ifs.finance.handler;

import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.handler.item.CostHandler;
import com.worth.ifs.finance.handler.item.JESCostHandler;
import com.worth.ifs.finance.repository.CostFieldRepository;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.resource.category.CostCategory;
import com.worth.ifs.finance.resource.category.DefaultCostCategory;
import com.worth.ifs.finance.resource.category.GrantClaimCategory;
import com.worth.ifs.finance.resource.cost.AcademicCost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.CostType;
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
    private CostRepository costRepository;

    @Autowired
    private CostFieldRepository costFieldRepository;

    @Override
    public Iterable<Cost> initialiseCostType(ApplicationFinance applicationFinance, CostType costType) {
        return null;
    }

    @Override
    public Map<CostType, CostCategory> getOrganisationFinances(Long applicationFinanceId) {
        List<Cost> costs = costRepository.findByApplicationFinanceId(applicationFinanceId);
        Map<CostType, CostCategory> costCategories = createCostCategories();
        return addCostsToCategories(costCategories, costs);
    }

    @Override
    public Map<CostType, CostCategory> getOrganisationFinanceTotals(Long applicationFinanceId, Competition competition) {
    	Map<CostType, CostCategory> costCategories = getOrganisationFinances(applicationFinanceId);
    	costCategories = setGrantClaimPercentage(costCategories, competition);
    	costCategories = calculateTotals(costCategories);
        return resetCosts(costCategories);
    }

    private Map<CostType, CostCategory> setGrantClaimPercentage(Map<CostType, CostCategory> costCategories, Competition competition) {
        CostItem costItem = new GrantClaim(0L, competition.getAcademicGrantPercentage());
        costCategories.get(CostType.FINANCE).addCost(costItem);
        return costCategories;
    }

    private Map<CostType, CostCategory> calculateTotals(Map<CostType, CostCategory> costCategories) {
        costCategories.values()
                .forEach(cc -> cc.calculateTotal());
        return costCategories;
    }

    private Map<CostType, CostCategory> resetCosts(Map<CostType, CostCategory> costCategories) {
        costCategories.values()
                .forEach(cc -> cc.setCosts(new ArrayList<>()));
        return costCategories;
    }

    private Map<CostType, CostCategory> createCostCategories() {
    	Map<CostType, CostCategory> costCategories = new EnumMap<>(CostType.class);

        for(CostType costType : CostType.values()) {
            CostCategory costCategory;
            switch (costType) {
                case FINANCE:
                    costCategory = new GrantClaimCategory();
                    break;
                default:
                    costCategory = new DefaultCostCategory();

            }
            costCategories.put(costType, costCategory);
        }
        return costCategories;
    }

    private Map<CostType, CostCategory> addCostsToCategories(Map<CostType, CostCategory> costCategories, List<Cost> costs) {
        costs.stream().forEach(c -> addCostToCategory(costCategories, c));
        return costCategories;
    }

    private void addCostToCategory(Map<CostType, CostCategory> costCategories, Cost cost) {
        CostType costType = CostType.fromString(cost.getQuestion().getFormInputs().get(0).getFormInputType().getTitle());
        CostItem costItem = toCostItem(cost);
        CostCategory costCategory = costCategories.get(costType);
        costCategory.addCost(costItem);
    }

    private CostItem toCostItem(Cost cost) {
        return new AcademicCost(cost.getId(), cost.getName(), cost.getCost(), cost.getItem());
    }

    @Override
    public Cost costItemToCost(CostItem costItem) {
        CostHandler costHandler = new JESCostHandler();
        List<CostField> costFields = costFieldRepository.findAll();
        costHandler.setCostFields(costFields);
        return costHandler.toCost(costItem);
    }

    @Override
    public CostItem costToCostItem(Cost cost) {
        CostHandler costHandler = new JESCostHandler();
        return costHandler.toCostItem(cost);
    }

    @Override
    public CostHandler getCostHandler(CostType costType) {
        return new JESCostHandler();
    }

    @Override
    public List<CostItem> costToCostItem(List<Cost> costs) {
        return costs.stream().map(c -> costToCostItem(c)).collect(Collectors.toList());
    }

    @Override
    public List<Cost> costItemsToCost(List<CostItem> costItems) {
        return costItems.stream().map(c -> costItemToCost(c)).collect(Collectors.toList());
    }
}
