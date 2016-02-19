package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.category.LabourCostCategory;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.LabourCost;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the labour costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class LabourCostHandler extends CostHandler {

    @Override
    public Cost toCost(CostItem costItem) {
        Cost cost = null;
        if (costItem instanceof LabourCost) {
            LabourCost labourCostItem = (LabourCost) costItem;
            cost = new Cost(labourCostItem.getId(), labourCostItem.getRole(), labourCostItem.getDescription(), labourCostItem.getLabourDays(), labourCostItem.getGrossAnnualSalary(), null, null);
        }
        return cost;
    }

    @Override
    public CostItem toCostItem(Cost cost) {
        return new LabourCost(cost.getId(), cost.getItem(), cost.getCost(), cost.getQuantity(), cost.getDescription());
    }

    @Override
    public List<Cost> initializeCost() {
        ArrayList<Cost> costs = new ArrayList<>();
        costs.add(initializeWorkingDays());
        return costs;
    }

    private Cost initializeWorkingDays() {
        String description = LabourCostCategory.WORKING_DAYS_PER_YEAR;
        Integer labourDays = null;
        LabourCost costItem = new LabourCost(null, null, null, labourDays, description);
        return toCost(costItem);
    }
}
