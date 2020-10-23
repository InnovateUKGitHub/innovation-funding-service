package org.innovateuk.ifs.supporter.workflow;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.supporter.domain.SupporterAssignment;
import org.innovateuk.ifs.supporter.domain.SupporterOutcome;
import org.innovateuk.ifs.supporter.repository.SupporterAssignmentRepository;
import org.innovateuk.ifs.supporter.resource.SupporterEvent;
import org.innovateuk.ifs.supporter.resource.SupporterState;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.workflow.BaseWorkflowEventHandler;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

@Component
public class SupporterAssignmentWorkflowHandler extends BaseWorkflowEventHandler<SupporterAssignment, SupporterState, SupporterEvent, Application, User> {

    @Autowired
    @Qualifier("supporterAssignmentStateMachineFactory")
    private StateMachineFactory<SupporterState, SupporterEvent> stateMachineFactory;

    @Autowired
    private SupporterAssignmentRepository supporterAssignmentRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected SupporterAssignment createNewProcess(Application target, User participant) {
        return new SupporterAssignment(target, participant);
    }

    public boolean reject(SupporterAssignment supporterAssignment, SupporterOutcome outcome) {
        return fireEvent(decisionMessage(supporterAssignment, outcome, SupporterEvent.REJECT), supporterAssignment);
    }

    public boolean accept(SupporterAssignment supporterAssignment, SupporterOutcome outcome) {
        return fireEvent(decisionMessage(supporterAssignment, outcome, SupporterEvent.ACCEPT), supporterAssignment);
    }

    public boolean edit(SupporterAssignment supporterAssignment) {
        return fireEvent(supporterMessage(supporterAssignment, SupporterEvent.EDIT), supporterAssignment);
    }

    @Override
    protected ProcessRepository<SupporterAssignment> getProcessRepository() {
        return supporterAssignmentRepository;
    }

    @Override
    protected CrudRepository<Application, Long> getTargetRepository() {
        return applicationRepository;
    }

    @Override
    protected CrudRepository<User, Long> getParticipantRepository() {
        return userRepository;
    }

    @Override
    protected StateMachineFactory<SupporterState, SupporterEvent> getStateMachineFactory() {
        return stateMachineFactory;
    }

    @Override
    protected SupporterAssignment getOrCreateProcess(Message<SupporterEvent> message) {
        return (SupporterAssignment) message.getHeaders().get("target");
    }

    private MessageBuilder<SupporterEvent> decisionMessage(SupporterAssignment supporterAssignment, SupporterOutcome decision, SupporterEvent event) {
        return supporterMessage(supporterAssignment, event)
                .setHeader("decision", decision);
    }


    private MessageBuilder<SupporterEvent> supporterMessage(SupporterAssignment supporterAssignment, SupporterEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", supporterAssignment);
    }
}