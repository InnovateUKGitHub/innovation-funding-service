package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.LabourCost;

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
}
