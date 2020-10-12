package org.innovateuk.ifs.cofunder.workflow;

import org.innovateuk.ifs.cofunder.domain.CofunderAssignment;
import org.innovateuk.ifs.cofunder.domain.CofunderOutcome;
import org.innovateuk.ifs.cofunder.resource.CofunderEvent;
import org.innovateuk.ifs.cofunder.resource.CofunderState;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Component
public class CofunderDecisionAction extends BaseCofunderAction {

    @Override
    protected void doExecute(CofunderAssignment assignment, StateContext<CofunderState, CofunderEvent> context) {
        CofunderOutcome outcome = (CofunderOutcome) context.getMessageHeader("decision");
        assignment.setCofunderOutcome(outcome);
    }
}
