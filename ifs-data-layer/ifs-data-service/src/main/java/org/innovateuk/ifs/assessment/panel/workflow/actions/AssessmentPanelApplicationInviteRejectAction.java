package org.innovateuk.ifs.assessment.panel.workflow.actions;

import org.innovateuk.ifs.assessment.panel.domain.AssessmentPanelApplicationInvite;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentPanelApplicationInviteRejectOutcome;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelApplicationInviteEvent;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelApplicationInviteState;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflow;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * The {@code AssessmentPanelApplicationInviteRejectAction} handles the reject event for an {@code AssessmentPanelApplicationInvite}
 * For more info see {@link AssessmentWorkflow}
 */
@Component
public class AssessmentPanelApplicationInviteRejectAction implements Action<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> {
    @Override
    public void execute(StateContext<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> context) {
        AssessmentPanelApplicationInvite invite = (AssessmentPanelApplicationInvite) context.getMessageHeader("target");
        AssessmentPanelApplicationInviteRejectOutcome rejectOutcome = (AssessmentPanelApplicationInviteRejectOutcome) context.getMessageHeader("rejection");
        invite.setRejection(rejectOutcome);
    }
}