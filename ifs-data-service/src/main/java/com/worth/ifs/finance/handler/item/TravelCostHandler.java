package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.CostType;
import com.worth.ifs.finance.resource.cost.TravelCost;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Handles the travel costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class TravelCostHandler extends CostHandler {
    private final Log log = LogFactory.getLog(getClass());

    @Override
    public Cost toCost(CostItem costItem) {
        Cost cost = null;
        log.info("COST TRAVEL UPDATE");
        if (costItem.getCostType().equals(CostType.TRAVEL)) {
            TravelCost travel = (TravelCost) costItem;
            cost = new Cost(travel.getId(), travel.getItem(), "", travel.getQuantity(), travel.getCost(), null, null);
        }
        return cost;
    }

    @Override
    public CostItem toCostItem(Cost cost) {
        return new TravelCost(cost.getId(), cost.getItem(), cost.getCost(), cost.getQuantity());
    }
}
