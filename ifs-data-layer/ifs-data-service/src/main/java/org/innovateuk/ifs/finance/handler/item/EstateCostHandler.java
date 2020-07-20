package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.EstateCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.ESTATE_COSTS;

/**
 * Handles the associate support costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class EstateCostHandler extends FinanceRowHandler<EstateCost> {

    @Override
    public ApplicationFinanceRow toApplicationDomain(EstateCost cost) {
        return new ApplicationFinanceRow(cost.getId(), cost.getName() , null, cost.getDescription(), null, cost.getTotal(), null, cost.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(EstateCost cost) {
        return new ProjectFinanceRow(cost.getId(), cost.getName() , null, cost.getDescription(), null, cost.getTotal(), null, cost.getCostType());
    }

    @Override
    public EstateCost toResource(FinanceRow cost) {
        return new EstateCost(cost.getTarget().getId(), cost.getId(), cost.getDescription(), cost.getCost().toBigInteger());
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(ESTATE_COSTS);
    }
}
