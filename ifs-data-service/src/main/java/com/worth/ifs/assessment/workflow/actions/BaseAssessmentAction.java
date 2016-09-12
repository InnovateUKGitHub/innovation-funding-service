package com.worth.ifs.assessment.workflow.actions;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.repository.ProcessOutcomeRepository;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.domain.State;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.util.Optional;

import static com.worth.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;

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
        Long processRoleId = (Long) context.getMessageHeader("processRoleId");
        ProcessOutcome updatedProcessOutcome = (ProcessOutcome) context.getMessageHeader("processOutcome");
        Assessment assessment = assessmentRepository.findOneByParticipantId(processRoleId);
        State newState = State.valueOf(context.getTransition().getTarget().getId());

        ActivityState newActivityState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, newState);
        doExecute(assessment, newActivityState, Optional.ofNullable(updatedProcessOutcome));
    }

    protected abstract void doExecute(Assessment assessment, ActivityState newState, Optional<ProcessOutcome> processOutcome);
}
