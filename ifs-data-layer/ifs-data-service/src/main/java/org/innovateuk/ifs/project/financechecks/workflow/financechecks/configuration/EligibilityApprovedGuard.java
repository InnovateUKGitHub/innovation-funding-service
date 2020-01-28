package org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration;

import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.finance.resource.EligibilityEvent;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EligibilityApprovedGuard implements Guard<EligibilityState, EligibilityEvent> {

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Override
    public boolean evaluate(StateContext<EligibilityState, EligibilityEvent> context) {
        PartnerOrganisation partnerOrganisation = (PartnerOrganisation) context.getMessage().getHeaders().get("target");
        return isFundingLevelWithinMaximum(
                projectFinanceService.financeChecksTotals(partnerOrganisation.getProject().getId()).getSuccess());
    }

    private boolean isFundingLevelWithinMaximum(List<ProjectFinanceResource> finances) {
        return finances.stream().anyMatch(finance ->
            finance.getMaximumFundingLevel() >= finance.getGrantClaimPercentage());
    }
}
