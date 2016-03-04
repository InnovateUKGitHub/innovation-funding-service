package com.worth.ifs.finance.handler;

import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.resource.category.CostCategory;
import com.worth.ifs.finance.resource.category.DefaultCostCategory;
import com.worth.ifs.finance.resource.cost.AcademicCost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.CostType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@Component
public class OrganisationJESFinance implements OrganisationFinanceHandler {
    EnumMap<CostType, CostCategory> costCategories = new EnumMap<>(CostType.class);

    @Autowired
    CostRepository costRepository;

    @Override
    public Iterable<Cost> initialiseCostType(ApplicationFinance applicationFinance, CostType costType) {
        return null;
    }

    @Override
    public EnumMap<CostType, CostCategory> getOrganisationFinances(Long applicationFinanceId) {
        List<Cost> costs = costRepository.findByApplicationFinanceId(applicationFinanceId);
        createCostCategories();
        addCostsToCategories(costs);
        return costCategories;
    }

    @Override
    public EnumMap<CostType, CostCategory> getOrganisationFinanceTotals(Long applicationFinanceId) {
        getOrganisationFinances(applicationFinanceId);
        calculateTotals();
        resetCosts();
        return costCategories;
    }

    private void calculateTotals() {
        costCategories.values()
                .forEach(cc -> cc.calculateTotal());
    }

    private void resetCosts() {
        costCategories.values()
                .forEach(cc -> cc.setCosts(new ArrayList<>()));
    }

    private void createCostCategories() {
        for(CostType costType : CostType.values()) {
            CostCategory costCategory = new DefaultCostCategory();
            costCategories.put(costType, costCategory);
        }
    }

    private void addCostsToCategories(List<Cost> costs) {
        costs.stream().forEach(c -> addCostToCategory(c));
    }

    private void addCostToCategory(Cost cost) {
        CostType costType = CostType.fromString(cost.getQuestion().getFormInputs().get(0).getFormInputType().getTitle());
        CostItem costItem = toCostItem(cost);
        CostCategory costCategory = costCategories.get(costType);
        costCategory.addCost(costItem);
    }

    private CostItem toCostItem(Cost cost) {
        return new AcademicCost(cost.getId(), cost.getCost(), cost.getDescription());
    }


    @Override
    public Cost costItemToCost(CostItem costItem) {
            return null;
    }

    @Override
    public CostItem costToCostItem(Cost cost) {
        return null;
    }
}
