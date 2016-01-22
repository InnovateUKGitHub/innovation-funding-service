package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.Overhead;

import java.math.BigDecimal;

/**
 * Handles the overheads, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class OverheadsHandler extends CostHandler {
    @Override
    public Cost toCost(CostItem costItem) {
        Cost cost = null;
        if(costItem instanceof Overhead) {
            Overhead overhead = (Overhead) costItem;
            cost = new Cost(overhead.getId(), overhead.getAcceptRate(), "", overhead.getCustomRate(), BigDecimal.ZERO, null, null);
        }
        return cost;
    }

    @Override
    public CostItem toCostItem(Cost cost) {
        return new Overhead(cost.getId(), cost.getItem(), cost.getQuantity());
    }
}
