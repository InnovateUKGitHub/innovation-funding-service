package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.AcademicCost;
import com.worth.ifs.finance.resource.cost.CostItem;

public class JESCostHandler extends CostHandler {
    @Override
    public Cost toCost(CostItem costItem) {
        Cost cost = null;
        if (costItem instanceof AcademicCost) {
            AcademicCost academicCostItem = (AcademicCost) costItem;
            cost = new Cost(academicCostItem.getId(), academicCostItem.getName(), academicCostItem.getItem(), null, null, academicCostItem.getTotal(), null, null);
        }
        return cost;
    }

    @Override
    public CostItem toCostItem(Cost cost) {
        return new AcademicCost(cost.getId(), cost.getName(), cost.getCost(), cost.getItem());
    }
}
