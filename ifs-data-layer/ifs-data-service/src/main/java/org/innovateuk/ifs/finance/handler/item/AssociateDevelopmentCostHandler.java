package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.AssociateDevelopmentCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS;

/**
 * Handles the associate support costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class AssociateDevelopmentCostHandler extends FinanceRowHandler<AssociateDevelopmentCost> {

    @Override
    public ApplicationFinanceRow toApplicationDomain(AssociateDevelopmentCost cost) {
        return new ApplicationFinanceRow(cost.getId(), cost.getName() , null, cost.getRole(), cost.getDuration(), cost.getTotal(), null, cost.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(AssociateDevelopmentCost cost) {
        return new ProjectFinanceRow(cost.getId(), cost.getName() , null, cost.getRole(), cost.getDuration(), cost.getTotal(), null, cost.getCostType());
    }

    @Override
    public AssociateDevelopmentCost toResource(FinanceRow cost) {
        return new AssociateDevelopmentCost(cost.getTarget().getId(), cost.getId(), cost.getDescription(), cost.getQuantity(), cost.getCost().toBigInteger());
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(ASSOCIATE_DEVELOPMENT_COSTS);
    }
}
