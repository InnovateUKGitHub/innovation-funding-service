package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.AcademicAndSecretarialSupport;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Handles the academic and secretarial support costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class AcademicAndSecretarialSupportHandler extends FinanceRowHandler<AcademicAndSecretarialSupport> {

    @Override
    public ApplicationFinanceRow toApplicationDomain(AcademicAndSecretarialSupport cost) {
        return new ApplicationFinanceRow(cost.getId(), cost.getName() , null, null, 1, cost.getTotal(), null, cost.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(AcademicAndSecretarialSupport cost) {
        return new ProjectFinanceRow(cost.getId(), cost.getName() , null, null, 1, cost.getTotal(), null, cost.getCostType());
    }

    @Override
    public AcademicAndSecretarialSupport toResource(FinanceRow cost) {
        return new AcademicAndSecretarialSupport(cost.getTarget().getId(), cost.getId(), bigIntegerOrNull(cost.getCost()));
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT);
    }
}
