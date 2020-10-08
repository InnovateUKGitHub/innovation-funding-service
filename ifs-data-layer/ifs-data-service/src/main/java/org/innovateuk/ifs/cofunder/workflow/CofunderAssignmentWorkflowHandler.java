package org.innovateuk.ifs.cofunder.workflow;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.cofunder.domain.CofunderAssignment;
import org.innovateuk.ifs.cofunder.domain.CofunderOutcome;
import org.innovateuk.ifs.cofunder.repository.CofunderAssignmentRepository;
import org.innovateuk.ifs.cofunder.resource.CofunderEvent;
import org.innovateuk.ifs.cofunder.resource.CofunderState;
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
public class CofunderAssignmentWorkflowHandler extends BaseWorkflowEventHandler<CofunderAssignment, CofunderState, CofunderEvent, Application, User> {

    @Autowired
    @Qualifier("cofunderAssignmentStateMachineFactory")
    private StateMachineFactory<CofunderState, CofunderEvent> stateMachineFactory;

    @Autowired
    private CofunderAssignmentRepository cofunderAssignmentRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected CofunderAssignment createNewProcess(Application target, User participant) {
        return new CofunderAssignment(target, participant);
    }

    public boolean reject(CofunderAssignment cofunderAssignment, CofunderOutcome outcome) {
        return fireEvent(decisionMessage(cofunderAssignment, outcome, CofunderEvent.REJECT), cofunderAssignment);
    }

    public boolean accept(CofunderAssignment cofunderAssignment, CofunderOutcome outcome) {
        return fireEvent(decisionMessage(cofunderAssignment, outcome, CofunderEvent.ACCEPT), cofunderAssignment);
    }

    public boolean edit(CofunderAssignment cofunderAssignment) {
        return fireEvent(cofunderMessage(cofunderAssignment, CofunderEvent.EDIT), cofunderAssignment);
    }

    @Override
    protected ProcessRepository<CofunderAssignment> getProcessRepository() {
        return cofunderAssignmentRepository;
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
    protected StateMachineFactory<CofunderState, CofunderEvent> getStateMachineFactory() {
        return stateMachineFactory;
    }

    @Override
    protected CofunderAssignment getOrCreateProcess(Message<CofunderEvent> message) {
        return (CofunderAssignment) message.getHeaders().get("target");
    }

    private MessageBuilder<CofunderEvent> decisionMessage(CofunderAssignment cofunderAssignment, CofunderOutcome decision, CofunderEvent event) {
        return cofunderMessage(cofunderAssignment, event)
                .setHeader("decision", decision);
    }


    private MessageBuilder<CofunderEvent> cofunderMessage(CofunderAssignment cofunderAssignment, CofunderEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", cofunderAssignment);
    }
}