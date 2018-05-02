package org.innovateuk.ifs.interview.workflow.actions;

import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentResponseOutcome;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentEvent;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

/**
 * The {@code FeedbackResponseInterviewAssignmentAction} is used when an interview assignment has been accepted and feedback is added by the applicant.
 * For more info see {@link InterviewAssignmentWorkflow}
 */
@Component
public class FeedbackResponseInterviewAssignmentAction extends BaseInterviewAssignmentAction {

    @Override
    protected void doExecute(InterviewAssignment interviewAssignment, StateContext<InterviewAssignmentState, InterviewAssignmentEvent> context) {
        InterviewAssignmentResponseOutcome interviewAssignmentResponseOutcome =
                (InterviewAssignmentResponseOutcome) context.getMessageHeader("response");

        interviewAssignment.setResponse(interviewAssignmentResponseOutcome);
    }
}