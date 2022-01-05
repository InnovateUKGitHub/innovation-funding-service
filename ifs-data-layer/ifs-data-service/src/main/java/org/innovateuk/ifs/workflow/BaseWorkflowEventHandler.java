package org.innovateuk.ifs.workflow;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.user.domain.ProcessActivity;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.domain.Process;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.innovateuk.ifs.workflow.resource.ProcessEvent;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction.testingStateTransition;

/**
 * A superclass for workflow handlers that expose public handler methods for pushing Process subclasses through
 * workflows
 */
@SuppressWarnings("unchecked")
@Slf4j
public abstract class BaseWorkflowEventHandler<
        ProcessType extends Process<ParticipantType, TargetType, StateType>,
        StateType extends ProcessState,
        EventType extends ProcessEvent,
        TargetType extends ProcessActivity,
        ParticipantType> {

    protected GenericPersistStateMachineHandler<StateType, EventType> stateHandler;

    @PostConstruct
    public void postConstruct() {
        stateHandler = new GenericPersistStateMachineHandler<>(getStateMachineFactory());
        stateHandler.addPersistStateChangeListener(new LocalStateChangeListener());
    }

    protected ProcessType getProcessByParticipantId(long participantId) {
        return getProcessRepository().findOneByParticipantId(participantId);
    }

    protected ProcessType getProcessByTargetId(long targetId) {
        return getProcessRepository().findOneByTargetId(targetId);
    }

    private class LocalStateChangeListener implements GenericPersistStateMachineHandler.GenericPersistStateChangeListener<StateType, EventType> {

        @Override
        public void onPersist(State<StateType, EventType> state, Message<EventType> message,
                              Transition<StateType, EventType> transition, StateMachine<StateType, EventType> stateMachine) {

            if (testingStateTransition(message)) {
                log.debug("TESTING STATE CHANGE: " + state.getId() + " transition: " + transition + " message: " + message + " transition: " + transition + " stateMachine " + stateMachine.getClass().getName());
                return;
            }

            log.debug("STATE: " + state.getId() + " transition: " + transition + " message: " + message + " transition: " + transition + " stateMachine " + stateMachine.getClass().getName());

            ProcessType processToUpdate = getOrCreateProcess(message);
            getParticipant(message).ifPresent(processToUpdate::setParticipant);
            getInternalParticipant(message).ifPresent(processToUpdate::setInternalParticipant);

            processToUpdate.setProcessState(state.getId());
            processToUpdate.setProcessEvent(message.getPayload().getType());
            processToUpdate.setLastModified(ZonedDateTime.now());
            getProcessRepository().save(processToUpdate);
        }
    }

    protected Optional<ParticipantType> getParticipant(Message<EventType> message) {
        return getOptionalParameterFromMessage("participant", message);
    }

    protected Optional<User> getInternalParticipant(Message<EventType> message) {
        return getOptionalParameterFromMessage("internalParticipant", message);
    }

    protected boolean fireEvent(MessageBuilder<EventType> event, TargetType target) {
        return fireEvent(event, getCurrentProcess(target));
    }

    protected boolean fireEvent(MessageBuilder<EventType> event, ProcessType process) {
        return fireEvent(event, process.getProcessState());
    }

    protected boolean fireEvent(MessageBuilder<EventType> event, StateType currentState) {
        return stateHandler.handleEventWithState(event.build(), currentState);
    }

    protected boolean testEvent(MessageBuilder<EventType> event, TargetType target) {
        return testEvent(event, getCurrentProcess(target).getProcessState());
    }

    protected boolean testEvent(MessageBuilder<EventType> event, StateType currentState) {
        return fireEvent(event.setHeader(TestableTransitionWorkflowAction.TESTING_GUARD_KEY, true), currentState);
    }

    protected ProcessType getCurrentProcess(TargetType target) {
        return getProcessByTargetId(target.getId());
    }

    protected ProcessType getOrCreateProcessCommonStrategy(Message<EventType> message) {

        TargetType target = (TargetType) message.getHeaders().get("target");

        Optional<ProcessType> existingProcess = Optional.ofNullable(getProcessByTargetId(target.getId()));

        ProcessType processToUpdate = existingProcess.orElseGet(() -> {
            ParticipantType participant = (ParticipantType) message.getHeaders().get("participant");
            return createNewProcess(target, participant);
        });

        return processToUpdate;
    }

    protected <T> Optional<T> getOptionalParameterFromMessage(String parameterName, Message<EventType> message) {
        return Optional.ofNullable((T) message.getHeaders().get(parameterName));
    }

    protected abstract ProcessType createNewProcess(TargetType target, ParticipantType participant);

    protected abstract ProcessRepository<ProcessType> getProcessRepository();

    protected abstract CrudRepository<TargetType, Long> getTargetRepository();

    protected abstract CrudRepository<ParticipantType, Long> getParticipantRepository();

    protected abstract StateMachineFactory<StateType, EventType> getStateMachineFactory();

    protected abstract ProcessType getOrCreateProcess(Message<EventType> message);
}