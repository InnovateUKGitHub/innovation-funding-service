package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.TravelCost;
import org.springframework.stereotype.Component;

/**
 * Handles the travel costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class TravelCostHandler extends FinanceRowHandler<TravelCost> {

    public static final String COST_KEY = "travel";

    @Override
    public ApplicationFinanceRow toApplicationDomain(TravelCost travel) {
        ApplicationFinanceRow applicationFinanceRow = null;
        if (travel != null && travel.getCostType() != null && travel.getCostType().equals(FinanceRowType.TRAVEL)) {
            applicationFinanceRow = new ApplicationFinanceRow(travel.getId(), COST_KEY, travel.getItem(), "", travel.getQuantity(), travel.getCost(), null, travel.getCostType());
        }
        return applicationFinanceRow;
    }

    @Override
    public ProjectFinanceRow toProjectDomain(TravelCost travel) {
        ProjectFinanceRow projectFinanceRow = null;
        if (travel != null && travel.getCostType() != null && travel.getCostType().equals(FinanceRowType.TRAVEL)) {
            projectFinanceRow =  new ProjectFinanceRow(travel.getId(), COST_KEY, travel.getItem(), "", travel.getQuantity(), travel.getCost(), null, travel.getCostType());
        }
        return projectFinanceRow;
    }

    @Override
    public FinanceRowItem toResource(FinanceRow cost) {
        return buildRowItem(cost);
    }

    private FinanceRowItem buildRowItem(FinanceRow cost){
        return new TravelCost(cost.getId(), cost.getItem(), cost.getCost(), cost.getQuantity(), cost.getTarget().getId());
    }
}
