package org.innovateuk.ifs.workflow;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import static org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction.testingStateTransition;

/**
 * {@code WorkflowStateMachineListener} for various state machine events.
 */
@Slf4j
public class WorkflowStateMachineListener<StateType, EventType> extends StateMachineListenerAdapter<StateType, EventType> {

    @Override
    public void eventNotAccepted(Message<EventType> event) {
        super.eventNotAccepted(event);

        if (!testingStateTransition(event)) {
            log.warn("Workflow event not accepted with payload: " + event.getPayload());
        }
    }

    @Override
    public void stateMachineError(StateMachine<StateType, EventType> stateMachine, Exception exception) {
        super.stateMachineError(stateMachine, exception);
        log.error("Workflow state machine error occurred", exception);
    }

    @Override
    public void stateChanged(State<StateType, EventType> from, State<StateType, EventType> to) {
        super.stateChanged(from, to);
        log.trace("Workflow state changed from [" + (from != null ? from.getId() : "no current state") + "] to [" + to.getId() + "]");
    }
}
