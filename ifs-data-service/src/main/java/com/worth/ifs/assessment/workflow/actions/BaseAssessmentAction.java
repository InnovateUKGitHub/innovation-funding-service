package com.worth.ifs.assessment.workflow.actions;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.repository.ProcessOutcomeRepository;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.workflow.TestableTransitionWorkflowAction;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import com.worth.ifs.workflow.resource.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;

import java.util.Optional;

import static com.worth.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;

/**
 * A base class for Assessment-related workflow Actions
 */
abstract class BaseAssessmentAction extends TestableTransitionWorkflowAction<AssessmentStates, AssessmentOutcomes> {

    @Autowired
    protected AssessmentRepository assessmentRepository;

    @Autowired
    protected ProcessOutcomeRepository processOutcomeRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Override
    public void doExecute(StateContext<AssessmentStates, AssessmentOutcomes> context) {
        Assessment assessment = getAssessmentFromContext(context);
        ProcessOutcome processOutcome = (ProcessOutcome) context.getMessageHeader("processOutcome");
        State newState = context.getTransition().getTarget().getId().getBackingState();

        ActivityState newActivityState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, newState);

        doExecute(assessment, Optional.ofNullable(processOutcome));

        assessment.setActivityState(newActivityState);
    }

    private Assessment getAssessmentFromContext(StateContext<AssessmentStates, AssessmentOutcomes> context) {
        return (Assessment) context.getMessageHeader("assessment");
    }

    protected abstract void doExecute(Assessment assessment, Optional<ProcessOutcome> processOutcome);
}