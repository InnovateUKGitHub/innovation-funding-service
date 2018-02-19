package org.innovateuk.ifs.interview.workflow.actions;

import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentEvent;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;

/**
 * A base class for Assessment Interview Panel-related workflow Actions
 */
public abstract class BaseInterviewAssignmentAction extends TestableTransitionWorkflowAction<InterviewAssignmentState, InterviewAssignmentEvent> {

    @Autowired
    protected InterviewAssignmentRepository interviewAssignmentRepository;

    @Override
    public void doExecute(StateContext<InterviewAssignmentState, InterviewAssignmentEvent> context) {
        InterviewAssignment interviewAssignment = getAssessmentInterviewPanelFromContext(context);
        doExecute(interviewAssignment, context);
    }

    private InterviewAssignment getAssessmentInterviewPanelFromContext(StateContext<InterviewAssignmentState, InterviewAssignmentEvent> context) {
        return (InterviewAssignment) context.getMessageHeader("target");
    }

    protected abstract void doExecute(InterviewAssignment assessment, StateContext<InterviewAssignmentState, InterviewAssignmentEvent> context);
}
