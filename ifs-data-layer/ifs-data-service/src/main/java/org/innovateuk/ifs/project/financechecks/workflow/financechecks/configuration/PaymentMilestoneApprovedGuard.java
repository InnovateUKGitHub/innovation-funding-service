package org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration;

import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneEvent;
import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
public class PaymentMilestoneApprovedGuard implements Guard<PaymentMilestoneState, PaymentMilestoneEvent> {

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private ViabilityWorkflowHandler viabilityWorkflowHandler;

    @Autowired
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;

    @Override
    public boolean evaluate(StateContext<PaymentMilestoneState, PaymentMilestoneEvent> context) {
        PartnerOrganisation partnerOrganisation = (PartnerOrganisation) context.getMessage().getHeaders().get("target");

        return eligibilityWorkflowHandler.getState(partnerOrganisation).isApprovedOrNotApplicable()
                && viabilityWorkflowHandler.getState(partnerOrganisation).isApprovedOrNotApplicable();
    }
}
