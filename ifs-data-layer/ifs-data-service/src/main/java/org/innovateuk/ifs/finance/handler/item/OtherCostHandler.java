package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.OtherCost;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Handles the other costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class OtherCostHandler extends FinanceRowHandler<OtherCost> {
    public static final String COST_KEY = "other-cost";

    @Override
    public ApplicationFinanceRow toCost(OtherCost otherCost) {
        return new ApplicationFinanceRow(otherCost.getId(), COST_KEY , "", otherCost.getDescription(), 0, otherCost.getCost(), null, null);
    }

    @Override
    public ProjectFinanceRow toProjectCost(OtherCost otherCost) {
        return new ProjectFinanceRow(otherCost.getId(), COST_KEY , "", otherCost.getDescription(), 0, otherCost.getCost(), null, null);
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
        return new OtherCost(cost.getId(),cost.getDescription(), cost.getCost());
    }

    @Override
    public List<ApplicationFinanceRow> initializeCost(ApplicationFinance applicationFinance) {
        Competition competition = applicationFinance.getApplication().getCompetition();
        ArrayList<ApplicationFinanceRow> costs = new ArrayList<>();
        if (competition.isH2020()) {
            costs.addAll(defaultHorizon2020Rows());
        }
        return costs;
    }

    private List<ApplicationFinanceRow> defaultHorizon2020Rows() {
        return asList(
                toCost(new OtherCost(null, "Labour costs", null)),
                toCost(new OtherCost(null, "Overhead costs", null)),
                toCost(new OtherCost(null, "Materials", null)),
                toCost(new OtherCost(null, "Capital usage", null)),
                toCost(new OtherCost(null, "Subcontracting costs", null)),
                toCost(new OtherCost(null, "Travel and substance", null)),
                toCost(new OtherCost(null, "Other costs", null))
        );
    }
}
