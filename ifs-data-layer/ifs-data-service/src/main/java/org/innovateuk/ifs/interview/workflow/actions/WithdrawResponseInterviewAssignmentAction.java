package org.innovateuk.ifs.interview.workflow.actions;

import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentResponseOutcome;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentResponseOutcomeRepository;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentEvent;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

/**
 * The {@code FeedbackResponseInterviewAssignmentAction} is used when an interview assignment response has been withdrawn.
 * For more info see {@link org.innovateuk.ifs.interview.workflow.configuration.InterviewAssignmentWorkflow}
 */
@Component
public class WithdrawResponseInterviewAssignmentAction extends BaseInterviewAssignmentAction {

    @Autowired
    private FileService fileService;

    @Autowired
    private InterviewAssignmentResponseOutcomeRepository interviewAssignmentResponseOutcomeRepository;

    @Override
    protected void doExecute(InterviewAssignment interviewAssignment, StateContext<InterviewAssignmentState, InterviewAssignmentEvent> context) {
        long fileId = interviewAssignment.getResponse().getFileResponse().getId();
        InterviewAssignmentResponseOutcome response = interviewAssignment.getResponse();
        fileService.deleteFileIgnoreNotFound(fileId).andOnSuccessReturnVoid(() -> {
            interviewAssignment.removeResponse();
            interviewAssignmentResponseOutcomeRepository.delete(response);
        });
    }
}