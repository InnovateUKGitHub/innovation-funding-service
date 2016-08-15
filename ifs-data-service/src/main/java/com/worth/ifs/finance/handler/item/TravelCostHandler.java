package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.FinanceRow;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import com.worth.ifs.finance.resource.cost.TravelCost;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Handles the travel costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class TravelCostHandler extends FinanceRowHandler {
    private static final Log LOG = LogFactory.getLog(TravelCostHandler.class);

    public static final String COST_KEY = "travel";

    @Override
    public FinanceRow toCost(FinanceRowItem costItem) {
        FinanceRow cost = null;
        LOG.info("COST TRAVEL UPDATE");
        if (costItem.getCostType().equals(FinanceRowType.TRAVEL)) {
            TravelCost travel = (TravelCost) costItem;
            cost = new FinanceRow(travel.getId(), COST_KEY, travel.getItem(), "", travel.getQuantity(), travel.getCost(), null, null);
        }
        return cost;
    }

    @Override
    public FinanceRowItem toCostItem(FinanceRow cost) {
        return new TravelCost(cost.getId(), cost.getItem(), cost.getCost(), cost.getQuantity());
    }
}
