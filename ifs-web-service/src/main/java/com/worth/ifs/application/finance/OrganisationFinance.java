package com.worth.ifs.application.finance;

import com.worth.ifs.application.finance.cost.CostItem;
import com.worth.ifs.domain.Cost;
import com.worth.ifs.service.CostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class OrganisationFinance {
    Long applicationFinanceId = 0L;
    EnumMap<CostType, CostCategory> costCategories = new EnumMap<>(CostType.class);
    List<Cost> costs = new ArrayList<>();

    CostItemFactory costItemFactory = new CostItemFactory();

    public OrganisationFinance() {
    }

    public OrganisationFinance(Long applicationFinanceId, List<Cost> costs) {
        this.applicationFinanceId = applicationFinanceId;
        this.costs = costs;
    }

    public EnumMap<CostType, CostCategory> getOrganisationFinances() {
        createCostCategories();
        addCostsToCategories(applicationFinanceId);
        return costCategories;
    }

    private void createCostCategories() {
        for(CostType costType : CostType.values()) {
            CostCategory costCategory = createCostCategoryByType(costType);
            costCategories.put(costType, costCategory);
        }
    }

    private CostCategory createCostCategoryByType(CostType costType) {
        switch(costType) {
            case LABOUR:
                return new LabourCostCategory();
            default:
                return new DefaultCostCategory();
        }
    }

    private void addCostsToCategories(Long applicationFinanceId) {
        costs.stream().forEach(c -> addCostToCategory(c));
    }

    private void addCostToCategory(Cost cost) {
        CostType costType = CostType.fromString(cost.getQuestion().getQuestionType().getTitle());
        CostCategory costCategory = costCategories.get(costType);
        CostItem costItem = costItemFactory.createCostItem(costType, cost);
        costCategory.addCost(costItem);
    }

    public Double getTotal() {
        return costCategories.entrySet().stream().mapToDouble(cat -> cat.getValue().getTotal()).sum();
    }

    public EnumMap<CostType, CostCategory> getCostCategories() {
        return costCategories;
    }

    public Long getApplicationFinanceId() {
        return applicationFinanceId;
    }
}
