package com.worth.ifs.workflow;

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

/**
 * A superclass for workflow handlers that expose public "service" methods for pushing Process subclasses through
 * workflows
 */
public abstract class BaseWorkflowEventHandler<ProcessType extends Process<ParticipantType, TargetType, StateType>, StateType extends ProcessStates, EventType extends OutcomeType, TargetType, ParticipantType> {

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

            ProcessType processToUpdate = getOrCreateProcess(message);
            ActivityState newState = activityStateRepository.findOneByActivityTypeAndState(getActivityType(), state.getId().getBackingState());
            processToUpdate.setActivityState(newState);
            processToUpdate.setProcessEvent(message.getPayload().getType());

            getProcessRepository().save(processToUpdate);
        }
    }

    private ProcessType getOrCreateProcess(Message<EventType> message) {

        Long targetId = (Long) message.getHeaders().get("targetId");

        ProcessType processToUpdate;
        ProcessType existingProcess = getProcessByTargetId(targetId);

        if (existingProcess != null) {
            processToUpdate = existingProcess;
        } else {
            Long participantId = (Long) message.getHeaders().get("participantId");
            processToUpdate = createNewProcess(targetId, participantId);
        }
        return processToUpdate;
    }

    private ProcessType createNewProcess(Long targetId, Long participantId) {
        TargetType target = getTargetRepository().findOne(targetId);
        ParticipantType participant = getParticipantRepository().findOne(participantId);
        return createNewProcess(target, participant);
    }

    protected abstract ProcessType createNewProcess(TargetType target, ParticipantType participant);

    protected abstract ActivityType getActivityType();

    protected abstract ProcessRepository<ProcessType> getProcessRepository();

    protected abstract CrudRepository<TargetType, Long> getTargetRepository();

    protected abstract CrudRepository<ParticipantType, Long> getParticipantRepository();

    protected abstract StateMachine<StateType, EventType> getStateMachine();

}