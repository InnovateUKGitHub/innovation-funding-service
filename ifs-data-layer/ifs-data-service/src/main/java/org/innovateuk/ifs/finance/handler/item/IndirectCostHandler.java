package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.IndirectCosts;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Handles the indirect costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class IndirectCostHandler extends FinanceRowHandler<IndirectCosts> {

    @Override
    public ApplicationFinanceRow toApplicationDomain(IndirectCosts cost) {
        return new ApplicationFinanceRow(cost.getId(), cost.getName() , null, null, 1, cost.getTotal(), null, cost.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(IndirectCosts cost) {
        return new ProjectFinanceRow(cost.getId(), cost.getName() , null, null, 1, cost.getTotal(), null, cost.getCostType());
    }

    @Override
    public IndirectCosts toResource(FinanceRow cost) {
        return new IndirectCosts(cost.getTarget().getId(), cost.getId(), bigIntegerOrNull(cost.getCost()));
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(FinanceRowType.INDIRECT_COSTS);
    }
}
