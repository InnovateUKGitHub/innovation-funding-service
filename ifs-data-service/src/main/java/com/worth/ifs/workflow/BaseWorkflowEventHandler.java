package com.worth.ifs.workflow;

import com.worth.ifs.invite.domain.ProcessActivity;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ActivityType;
import com.worth.ifs.workflow.domain.Process;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import com.worth.ifs.workflow.repository.ProcessRepository;
import com.worth.ifs.workflow.resource.OutcomeType;
import com.worth.ifs.workflow.resource.ProcessStates;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

import javax.annotation.PostConstruct;
import java.util.Optional;

import static org.springframework.statemachine.state.PseudoStateKind.CHOICE;

/**
 * A superclass for workflow handlers that expose public handler methods for pushing Process subclasses through
 * workflows
 */
public abstract class BaseWorkflowEventHandler<ProcessType extends Process<ParticipantType, TargetType, StateType>, StateType extends ProcessStates, EventType extends OutcomeType, TargetType extends ProcessActivity, ParticipantType> {

    private static final Log LOG = LogFactory.getLog(BaseWorkflowEventHandler.class);

    @Autowired
    private ActivityStateRepository activityStateRepository;

    protected GenericPersistStateMachineHandler<StateType, EventType> stateHandler;

    @PostConstruct
    public void postConstruct() {
        stateHandler = new GenericPersistStateMachineHandler<>(getStateMachine());
        stateHandler.addPersistStateChangeListener(new LocalStateChangeListener());
    }

    protected ProcessType getProcessByParticipantId(Long participantId) {
        return getProcessRepository().findOneByParticipantId(participantId);
    }

    protected ProcessType getProcessByTargetId(Long targetId) {
        return getProcessRepository().findOneByTargetId(targetId);
    }

    private class LocalStateChangeListener implements GenericPersistStateMachineHandler.GenericPersistStateChangeListener<StateType, EventType> {

        @Override
        public void onPersist(State<StateType, EventType> state, Message<EventType> message,
                              Transition<StateType, EventType> transition, StateMachine<StateType, EventType> stateMachine) {

            LOG.debug("STATE: " + state.getId() + " transition: " + transition + " message: " + message + " transition: " + transition + " stateMachine " + stateMachine.getClass().getName());

            if (state.getPseudoState() == null || state.getPseudoState().getKind() != CHOICE) {

                ProcessType processToUpdate = getOrCreateProcess(message);
                ActivityState newState = activityStateRepository.findOneByActivityTypeAndState(getActivityType(), state.getId().getBackingState());
                processToUpdate.setActivityState(newState);
                processToUpdate.setProcessEvent(message.getPayload().getType());

                getProcessRepository().save(processToUpdate);
            }
        }
    }

    private ProcessType getOrCreateProcess(Message<EventType> message) {

        TargetType target = (TargetType) message.getHeaders().get("target");

        Optional<ProcessType> existingProcess = Optional.ofNullable(getProcessByTargetId(target.getId()));

        ProcessType processToUpdate = existingProcess.orElseGet(() -> {
            ParticipantType participant = (ParticipantType) message.getHeaders().get("participant");
            return createNewProcess(target, participant);
        });

        return processToUpdate;
    }

    protected abstract ProcessType createNewProcess(TargetType target, ParticipantType participant);

    protected abstract ActivityType getActivityType();

    protected abstract ProcessRepository<ProcessType> getProcessRepository();

    protected abstract CrudRepository<TargetType, Long> getTargetRepository();

    protected abstract CrudRepository<ParticipantType, Long> getParticipantRepository();

    protected abstract StateMachine<StateType, EventType> getStateMachine();

}