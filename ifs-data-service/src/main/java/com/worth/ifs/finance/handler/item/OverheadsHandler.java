package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.category.OverheadCostCategory;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.Overhead;
import com.worth.ifs.finance.resource.cost.OverheadRateType;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the overheads, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class OverheadsHandler extends CostHandler {
    @Override
    public Cost toCost(CostItem costItem) {
        Cost cost = null;
        if (costItem instanceof Overhead) {
            Overhead overhead = (Overhead) costItem;
            Integer rate = overhead.getRate();
            String rateType = null;

            if (overhead.getRateType() != null) {
                rateType = overhead.getRateType().toString();
            }

            cost = new Cost(overhead.getId(), rateType, "", rate, null, null, null);
        }
        return cost;
    }

    @Override
    public CostItem toCostItem(Cost cost) {
        OverheadRateType type = (OverheadRateType.valueOf(cost.getItem()) != null ? OverheadRateType.valueOf(cost.getItem()) : OverheadRateType.NONE);
        return new Overhead(cost.getId(), type, cost.getQuantity());
    }

    @Override
    public List<Cost> initializeCost() {
        ArrayList<Cost> costs = new ArrayList<>();
        costs.add(initializeAcceptRate());
        return costs;
    }

    private Cost initializeAcceptRate() {
        Overhead costItem = new Overhead();
        Cost cost = toCost(costItem);
        cost.setDescription(OverheadCostCategory.ACCEPT_RATE);
        return cost;
    }
}
