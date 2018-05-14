package org.innovateuk.ifs.interview.workflow.configuration;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentMessageOutcome;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentResponseOutcome;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentEvent;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.workflow.BaseWorkflowEventHandler;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

/**
 * Manages the process for assigning applications to assessors for an assessment interview.
 */
@Component
public class InterviewAssignmentWorkflowHandler extends BaseWorkflowEventHandler<InterviewAssignment, InterviewAssignmentState, InterviewAssignmentEvent, Application, ProcessRole> {

    @Autowired
    @Qualifier("assessmentInterviewPanelStateMachineFactory")
    private StateMachineFactory<InterviewAssignmentState, InterviewAssignmentEvent> stateMachineFactory;

    @Autowired
    private InterviewAssignmentRepository interviewAssignmentRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Override
    protected InterviewAssignment createNewProcess(Application application, ProcessRole participant) {
        return new InterviewAssignment(application, participant);
    }

    public boolean notifyInterviewPanel(InterviewAssignment interviewAssignment, InterviewAssignmentMessageOutcome messageOutcome) {
        return fireEvent(notifyMessage(interviewAssignment, messageOutcome), interviewAssignment);
    }

    public boolean respondToInterviewPanel(InterviewAssignment interviewAssignment, InterviewAssignmentResponseOutcome responseOutcome) {
        return fireEvent(responseMessage(interviewAssignment, responseOutcome), interviewAssignment);
    }

    public boolean withdrawResponse(InterviewAssignment interviewAssignment) {
        return fireEvent(withdrawResponseMessage(interviewAssignment), interviewAssignment);
    }

    @Override
    protected ProcessRepository<InterviewAssignment> getProcessRepository() {
        return interviewAssignmentRepository;
    }

    @Override
    protected CrudRepository<Application, Long> getTargetRepository() {
        return applicationRepository;
    }

    @Override
    protected CrudRepository<ProcessRole, Long> getParticipantRepository() {
        return processRoleRepository;
    }

    @Override
    protected StateMachineFactory<InterviewAssignmentState, InterviewAssignmentEvent> getStateMachineFactory() {
        return stateMachineFactory;
    }

    @Override
    protected InterviewAssignment getOrCreateProcess(Message<InterviewAssignmentEvent> message) {
        return (InterviewAssignment) message.getHeaders().get("target");
    }

    private MessageBuilder<InterviewAssignmentEvent> notifyMessage(InterviewAssignment interviewAssignment, InterviewAssignmentMessageOutcome messageOutcome) {
        return interviewAssignmentMessage(interviewAssignment, InterviewAssignmentEvent.NOTIFY)
                .setHeader("message", messageOutcome);
    }

    private MessageBuilder<InterviewAssignmentEvent> responseMessage(InterviewAssignment interviewAssignment, InterviewAssignmentResponseOutcome responseOutcome) {
        return interviewAssignmentMessage(interviewAssignment, InterviewAssignmentEvent.RESPOND)
                .setHeader("response", responseOutcome);
    }

    private MessageBuilder<InterviewAssignmentEvent> withdrawResponseMessage(InterviewAssignment interviewAssignment) {
        return interviewAssignmentMessage(interviewAssignment, InterviewAssignmentEvent.WITHDRAW_RESPONSE);
    }

    private static MessageBuilder<InterviewAssignmentEvent> interviewAssignmentMessage(InterviewAssignment interviewAssignment, InterviewAssignmentEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", interviewAssignment);
    }
}