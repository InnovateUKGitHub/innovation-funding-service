package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.OtherCost;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.OTHER_COSTS;

/**
 * Handles the other costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class OtherCostHandler extends FinanceRowHandler<OtherCost> {
    public static final String COST_KEY = "other-cost";

    @Override
    public ApplicationFinanceRow toApplicationDomain(OtherCost otherCost) {
        return new ApplicationFinanceRow(otherCost.getId(), COST_KEY , "", otherCost.getDescription(), 0, otherCost.getCost(), null, otherCost.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(OtherCost otherCost) {
        return new ProjectFinanceRow(otherCost.getId(), COST_KEY , "", otherCost.getDescription(), 0, otherCost.getCost(), null, otherCost.getCostType());
    }

    @Override
    public OtherCost toResource(FinanceRow cost) {
        return new OtherCost(cost.getId(),cost.getDescription(), cost.getCost(), cost.getTarget().getId());
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(OTHER_COSTS);
    }
}
