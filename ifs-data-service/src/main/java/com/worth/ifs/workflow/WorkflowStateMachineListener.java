package com.worth.ifs.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import static com.worth.ifs.workflow.TestableTransitionWorkflowAction.testingStateTransition;

/**
 * {@code WorkflowStateMachineListener} for various state machine events.
 */
public class WorkflowStateMachineListener<StateType, EventType> extends StateMachineListenerAdapter<StateType, EventType> {

    private static final Log LOG = LogFactory.getLog(WorkflowStateMachineListener.class);

    @Override
    public void eventNotAccepted(Message<EventType> event) {
        super.eventNotAccepted(event);

        if (!testingStateTransition(event)) {
            LOG.warn("Workflow event not accepted with payload: " + event.getPayload());
        }
    }

    @Override
    public void stateMachineError(StateMachine<StateType, EventType> stateMachine, Exception exception) {
        super.stateMachineError(stateMachine, exception);
        LOG.error("Workflow state machine error occurred", exception);
    }

    @Override
    public void stateChanged(State<StateType, EventType> from, State<StateType, EventType> to) {
        super.stateChanged(from, to);
        LOG.trace("Workflow state changed from [" + (from != null ? from.getId() : "no current state") + "] to [" + to.getId() + "]");
    }
}