package org.innovateuk.ifs.supporter.workflow;

import org.innovateuk.ifs.supporter.domain.SupporterAssignment;
import org.innovateuk.ifs.supporter.domain.SupporterOutcome;
import org.innovateuk.ifs.supporter.resource.SupporterEvent;
import org.innovateuk.ifs.supporter.resource.SupporterState;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Component
public class SupporterDecisionAction extends BaseSupporterAction {

    @Override
    protected void doExecute(SupporterAssignment assignment, StateContext<SupporterState, SupporterEvent> context) {
        SupporterOutcome outcome = (SupporterOutcome) context.getMessageHeader("decision");
        assignment.setSupporterOutcome(outcome);
    }
}
