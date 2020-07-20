package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.AssociateSalaryCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.ASSOCIATE_SALARY_COSTS;

/**
 * Handles the associate salary costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class AssociateSalaryCostHandler extends FinanceRowHandler<AssociateSalaryCost> {

    @Override
    public ApplicationFinanceRow toApplicationDomain(AssociateSalaryCost cost) {
        return new ApplicationFinanceRow(cost.getId(), cost.getName() , null, cost.getRole(), null, cost.getTotal(), null, cost.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(AssociateSalaryCost cost) {
        return new ProjectFinanceRow(cost.getId(), cost.getName() , null, cost.getRole(), null, cost.getTotal(), null, cost.getCostType());
    }

    @Override
    public AssociateSalaryCost toResource(FinanceRow cost) {
        return new AssociateSalaryCost(cost.getTarget().getId(), cost.getId(), cost.getDescription(), cost.getCost().toBigInteger());
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(ASSOCIATE_SALARY_COSTS);
    }
}
