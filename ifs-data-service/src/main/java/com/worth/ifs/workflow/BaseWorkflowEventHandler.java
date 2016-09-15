package com.worth.ifs.workflow;

import com.worth.ifs.workflow.repository.ProcessRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler.PersistStateChangeListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

/**
 * {@code ProjectDetailsWorkflowEventHandler} is the entry point for triggering the workflow.
 * Based on the Project Detail's current state the next one is tried to transition to by triggering
 * an event.
 */
public class BaseWorkflowEventHandler<ProcessType> {

    private static final Log LOG = LogFactory.getLog(BaseWorkflowEventHandler.class);

    protected PersistStateMachineHandler stateHandler;
    private PersistStateChangeListener listener = new LocalStateChangeListener();
    private ProcessRepository<ProcessType> processRepository;

    public BaseWorkflowEventHandler(PersistStateMachineHandler stateHandler, ProcessRepository<ProcessType> processRepository) {
        this.stateHandler = stateHandler;
        this.processRepository = processRepository;
        this.stateHandler.addPersistStateChangeListener(listener);
    }

    protected ProcessType getProcessByParticipantId(Long participantId) {
        return processRepository.findOneByParticipantId(participantId);
    }

    protected ProcessType getProcessByTargetId(Long targetId) {
        return processRepository.findOneByParticipantId(targetId);
    }

    private class LocalStateChangeListener implements PersistStateChangeListener {

        @Override
        public void onPersist(State<String, String> state, Message<String> message,
                              Transition<String, String> transition, StateMachine<String, String> stateMachine) {

            LOG.info("STATE: " + state.getId() + " transition: " + transition + " message: " + message + " transition: " + transition + " stateMachine " + stateMachine.getClass().getName());
        }
    }
}
