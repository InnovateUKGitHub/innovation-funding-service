package com.worth.ifs.assessment.workflow;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.workflow.BaseWorkflowEventHandler;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;

/**
 * {@code AssessmentWorkflowEventHandler} is the entry point for triggering the workflow.
 * Based on the assessment's current state the next one is tried to transition to by triggering
 * an event.
 */
public class AssessmentWorkflowEventHandler extends BaseWorkflowEventHandler {

    public AssessmentWorkflowEventHandler(PersistStateMachineHandler stateHandler) {
        super(stateHandler);
    }

    public boolean rejectInvitation(Long processRoleId, Assessment assessment, ProcessOutcome processOutcome) {
        return stateHandler.handleEventWithState(MessageBuilder
                .withPayload(AssessmentOutcomes.REJECT.getType())
                .setHeader("processRoleId", processRoleId)
                .setHeader("processOutcome", processOutcome)
                .build(), assessment.getActivityState().getBackingState().name());
    }

    public boolean recommend(Long processRoleId, Assessment assessment, ProcessOutcome processOutcome) {
        return stateHandler.handleEventWithState(MessageBuilder
                .withPayload(AssessmentOutcomes.RECOMMEND.getType())
                .setHeader("assessment", assessment)
                .setHeader("processRoleId", processRoleId)
                .setHeader("processOutcome", processOutcome)
                .build(), assessment.getActivityState().getBackingState().name());
    }
}
