package com.worth.ifs.assessment.workflow.actions;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.repository.ProcessOutcomeRepository;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.workflow.TestableTransitionWorkflowAction;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;

import java.util.Optional;

/**
 * A base class for Assessment-related workflow Actions
 */
public abstract class BaseAssessmentAction extends TestableTransitionWorkflowAction<AssessmentStates, AssessmentOutcomes> {

    @Autowired
    protected AssessmentRepository assessmentRepository;

    @Autowired
    protected ProcessOutcomeRepository processOutcomeRepository;

    @Override
    public void doExecute(StateContext<AssessmentStates, AssessmentOutcomes> context) {
        Assessment assessment = getAssessmentFromContext(context);
        ProcessOutcome processOutcome = (ProcessOutcome) context.getMessageHeader("processOutcome");
        doExecute(assessment, Optional.ofNullable(processOutcome));
    }

    private Assessment getAssessmentFromContext(StateContext<AssessmentStates, AssessmentOutcomes> context) {
        return (Assessment) context.getMessageHeader("assessment");
    }

    protected abstract void doExecute(Assessment assessment, Optional<ProcessOutcome> processOutcome);
}