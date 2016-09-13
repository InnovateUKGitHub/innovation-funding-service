package com.worth.ifs.assessment.workflow.actions;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.repository.ProcessOutcomeRepository;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.resource.State;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.util.Optional;

import static com.worth.ifs.workflow.resource.ActivityType.APPLICATION_ASSESSMENT;

/**
 * A base class for Assessment-related workflow Actions
 */
abstract class BaseAssessmentAction implements Action<String, String> {

    @Autowired
    protected AssessmentRepository assessmentRepository;

    @Autowired
    protected ProcessOutcomeRepository processOutcomeRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Override
    public void execute(StateContext<String, String> context) {

        Assessment assessment = getAssessmentFromContext(context);
        ProcessOutcome updatedProcessOutcome = (ProcessOutcome) context.getMessageHeader("processOutcome");
        State newState = State.valueOf(context.getTransition().getTarget().getId());

        ActivityState newActivityState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, newState);
        doExecute(assessment, newActivityState, Optional.ofNullable(updatedProcessOutcome));
    }

    private Assessment getAssessmentFromContext(StateContext<String, String> context) {

        Assessment assessmentInContext = (Assessment) context.getMessageHeader("assessment");

        if (assessmentInContext != null) {
            return assessmentInContext;
        } else {
            Long processRoleId = (Long) context.getMessageHeader("processRoleId");
            return assessmentRepository.findOneByParticipantId(processRoleId);
        }
    }

    protected abstract void doExecute(Assessment assessment, ActivityState newState, Optional<ProcessOutcome> processOutcome);
}
