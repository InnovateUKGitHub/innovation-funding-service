package com.worth.ifs.workflow;

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
public class BaseWorkflowEventHandler {

    private static final Log LOG = LogFactory.getLog(BaseWorkflowEventHandler.class);
    protected final PersistStateMachineHandler stateHandler;
    private final PersistStateChangeListener listener = new LocalStateChangeListener();

    public BaseWorkflowEventHandler(PersistStateMachineHandler stateHandler) {
        this.stateHandler = stateHandler;
        this.stateHandler.addPersistStateChangeListener(listener);
    }

    public boolean canMoveToState(State state) {
        return stateHandler.
    }

    private class LocalStateChangeListener implements PersistStateChangeListener {

        @Override
        public void onPersist(State<String, String> state, Message<String> message,
                              Transition<String, String> transition, StateMachine<String, String> stateMachine) {

            LOG.info("STATE: " + state.getId() + " transition: " + transition + " message: " + message + " transition: " + transition + " stateMachine " + stateMachine.getClass().getName());
        }
    }
}
