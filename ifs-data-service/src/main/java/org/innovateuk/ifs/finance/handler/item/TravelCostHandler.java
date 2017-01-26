package org.innovateuk.ifs.finance.handler.item;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.TravelCost;

/**
 * Handles the travel costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class TravelCostHandler extends FinanceRowHandler {
    private static final Log LOG = LogFactory.getLog(TravelCostHandler.class);

    public static final String COST_KEY = "travel";

    @Override
    public ApplicationFinanceRow toCost(FinanceRowItem costItem) {
        ApplicationFinanceRow cost = null;
        LOG.info("COST TRAVEL UPDATE");
        if (costItem.getCostType().equals(FinanceRowType.TRAVEL)) {
            TravelCost travel = (TravelCost) costItem;
            cost = new ApplicationFinanceRow(travel.getId(), COST_KEY, travel.getItem(), "", travel.getQuantity(), travel.getCost(), null, null);
        }
        return cost;
    }

    @Override
    public ProjectFinanceRow toProjectCost(FinanceRowItem costItem) {
        ProjectFinanceRow cost = null;
        LOG.info("COST TRAVEL UPDATE");
        if (costItem.getCostType().equals(FinanceRowType.TRAVEL)) {
            TravelCost travel = (TravelCost) costItem;
            cost = new ProjectFinanceRow(travel.getId(), COST_KEY, travel.getItem(), "", travel.getQuantity(), travel.getCost(), null, null);
        }
        return cost;
    }

    @Override
    public FinanceRowItem toCostItem(ApplicationFinanceRow cost) {
        return buildRowItem(cost);
    }

    @Override
    public FinanceRowItem toCostItem(ProjectFinanceRow cost) {
        return buildRowItem(cost);
    }

    private FinanceRowItem buildRowItem(FinanceRow cost){
        return new TravelCost(cost.getId(), cost.getItem(), cost.getCost(), cost.getQuantity());
    }
}
