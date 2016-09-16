package com.worth.ifs.assessment.workflow.configuration;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.workflow.BaseWorkflowEventHandler;
import com.worth.ifs.workflow.GenericPersistStateMachineHandler;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.springframework.messaging.support.MessageBuilder;

/**
 * {@code AssessmentWorkflowService} is the entry point for triggering the workflow.
 * Based on the assessment's current state the next one is tried to transition to by triggering
 * an event.
 */
public class AssessmentWorkflowService extends BaseWorkflowEventHandler<Assessment, AssessmentStates, String> {

    public AssessmentWorkflowService(GenericPersistStateMachineHandler<AssessmentStates, String> stateHandler, AssessmentRepository assessmentRepository) {
        super(stateHandler, assessmentRepository);
    }

    public boolean rejectInvitation(Long processRoleId, Assessment assessment, ProcessOutcome processOutcome) {
        return stateHandler.handleEventWithState(MessageBuilder
                .withPayload(AssessmentOutcomes.REJECT.getType())
                .setHeader("processRoleId", processRoleId)
                .setHeader("processOutcome", processOutcome)
                .build(), assessment.getActivityState());
    }

    public boolean recommend(Long processRoleId, Assessment assessment, ProcessOutcome processOutcome) {
        return stateHandler.handleEventWithState(MessageBuilder
                .withPayload(AssessmentOutcomes.RECOMMEND.getType())
                .setHeader("assessment", assessment)
                .setHeader("processRoleId", processRoleId)
                .setHeader("processOutcome", processOutcome)
                .build(), assessment.getActivityState());
    }
}
