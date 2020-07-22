package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.KnowledgeBaseCost;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.KNOWLEDGE_BASE;

/**
 * Handles the associate salary costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class KnowledgeBaseCostHandler extends FinanceRowHandler<KnowledgeBaseCost> {

    @Override
    public ApplicationFinanceRow toApplicationDomain(KnowledgeBaseCost cost) {
        return new ApplicationFinanceRow(cost.getId(), cost.getName() , null, cost.getDescription(), null, cost.getTotal(), null, cost.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(KnowledgeBaseCost cost) {
        return new ProjectFinanceRow(cost.getId(), cost.getName() , null, cost.getDescription(), null, cost.getTotal(), null, cost.getCostType());
    }

    @Override
    public KnowledgeBaseCost toResource(FinanceRow cost) {
        return new KnowledgeBaseCost(cost.getTarget().getId(), cost.getId(), cost.getDescription(), bigIntegerOrNull(cost.getCost()));
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(KNOWLEDGE_BASE);
    }
}
