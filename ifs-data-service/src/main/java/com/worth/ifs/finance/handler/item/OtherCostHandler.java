package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.OtherCost;

public class OtherCostHandler extends CostHandler {
    @Override
    public Cost toCost(CostItem costItem) {
        Cost cost = null;
        if (costItem instanceof OtherCost) {
            OtherCost otherCost = (OtherCost) costItem;
            cost = new Cost(otherCost.getId(), "", otherCost.getDescription(), 0, otherCost.getCost(), null, null);
        }
        return cost;
    }

    @Override
    public CostItem toCostItem(Cost cost) {
        return new OtherCost(cost.getId(),cost.getDescription(), cost.getCost());
    }
}
