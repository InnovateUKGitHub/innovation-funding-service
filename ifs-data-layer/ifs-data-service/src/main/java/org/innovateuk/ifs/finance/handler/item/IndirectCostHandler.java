package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.IndirectCost;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Handles the indirect costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class IndirectCostHandler extends FinanceRowHandler<IndirectCost> {

    @Override
    public ApplicationFinanceRow toApplicationDomain(IndirectCost cost) {
        return new ApplicationFinanceRow(cost.getId(), cost.getName() , null, null, 1, cost.getTotal(), null, cost.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(IndirectCost cost) {
        return new ProjectFinanceRow(cost.getId(), cost.getName() , null, null, 1, cost.getTotal(), null, cost.getCostType());
    }

    @Override
    public IndirectCost toResource(FinanceRow cost) {
        return new IndirectCost(cost.getTarget().getId(), cost.getId(), bigIntegerOrNull(cost.getCost()));
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(FinanceRowType.INDIRECT_COSTS);
    }
}
