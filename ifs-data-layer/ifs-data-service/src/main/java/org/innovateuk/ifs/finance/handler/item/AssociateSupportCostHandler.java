package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.AssociateSupportCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.ASSOCIATE_SUPPORT;

/**
 * Handles the associate support costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class AssociateSupportCostHandler extends FinanceRowHandler<AssociateSupportCost> {

    @Override
    public ApplicationFinanceRow toApplicationDomain(AssociateSupportCost cost) {
        return new ApplicationFinanceRow(cost.getId(), cost.getName() , null, cost.getDescription(), null, cost.getTotal(), null, cost.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(AssociateSupportCost cost) {
        return new ProjectFinanceRow(cost.getId(), cost.getName() , null, cost.getDescription(), null, cost.getTotal(), null, cost.getCostType());
    }

    @Override
    public AssociateSupportCost toResource(FinanceRow cost) {
        return new AssociateSupportCost(cost.getTarget().getId(), cost.getId(), cost.getDescription(), cost.getCost().toBigInteger());
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(ASSOCIATE_SUPPORT);
    }
}
