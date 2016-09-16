package com.worth.ifs.workflow;

import com.worth.ifs.workflow.repository.ProcessRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

/**
 * A superclass for workflow handlers that expose public "service" methods for pushing Process subclasses through
 * workflows
 */
public class BaseWorkflowEventHandler<ProcessType, StateType, EventType> {

    private static final Log LOG = LogFactory.getLog(BaseWorkflowEventHandler.class);

    protected GenericPersistStateMachineHandler<StateType, EventType> stateHandler;
    private ProcessRepository<ProcessType> processRepository;

    public BaseWorkflowEventHandler(GenericPersistStateMachineHandler<StateType, EventType> stateHandler, ProcessRepository<ProcessType> processRepository) {
        this.stateHandler = stateHandler;
        this.processRepository = processRepository;
        this.stateHandler.addPersistStateChangeListener(new LocalStateChangeListener());
    }

    protected ProcessType getProcessByParticipantId(Long participantId) {
        return processRepository.findOneByParticipantId(participantId);
    }

    protected ProcessType getProcessByTargetId(Long targetId) {
        return processRepository.findOneByParticipantId(targetId);
    }

    private class LocalStateChangeListener implements GenericPersistStateMachineHandler.GenericPersistStateChangeListener<StateType, EventType> {

        @Override
        public void onPersist(State<StateType, EventType> state, Message<EventType> message,
                              Transition<StateType, EventType> transition, StateMachine<StateType, EventType> stateMachine) {

            LOG.info("STATE: " + state.getId() + " transition: " + transition + " message: " + message + " transition: " + transition + " stateMachine " + stateMachine.getClass().getName());
        }
    }
}
