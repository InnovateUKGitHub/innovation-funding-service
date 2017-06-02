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
public class TravelCostHandler extends FinanceRowHandler<TravelCost> {
    private static final Log LOG = LogFactory.getLog(TravelCostHandler.class);

    public static final String COST_KEY = "travel";

    @Override
    public ApplicationFinanceRow toCost(TravelCost travel) {
        ApplicationFinanceRow applicationFinanceRow = null;
        if (travel != null && travel.getCostType() != null && travel.getCostType().equals(FinanceRowType.TRAVEL)) {
            applicationFinanceRow = new ApplicationFinanceRow(travel.getId(), COST_KEY, travel.getItem(), "", travel.getQuantity(), travel.getCost(), null, null);
        }
        return applicationFinanceRow;
    }

    @Override
    public ProjectFinanceRow toProjectCost(TravelCost travel) {
        ProjectFinanceRow projectFinanceRow = null;
        if (travel != null && travel.getCostType() != null && travel.getCostType().equals(FinanceRowType.TRAVEL)) {
            projectFinanceRow =  new ProjectFinanceRow(travel.getId(), COST_KEY, travel.getItem(), "", travel.getQuantity(), travel.getCost(), null, null);
        }
        return projectFinanceRow;
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
