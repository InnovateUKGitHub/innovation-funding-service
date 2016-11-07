package com.worth.ifs.workflow;

import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * A standard workflow Action with the important distinction that subclassing this Action type allows us to test
 * whether or not transitions are possible without actually executing the final successful Action.
 */
public abstract class TestableTransitionWorkflowAction<StateType, EventType> implements Action<StateType, EventType> {

    static final String TESTING_GUARD_KEY = "com.worth.ifs.workflow__TESTING_GUARD_KEY__";

    @Override
    public final void execute(StateContext<StateType, EventType> context) {
        if (!testingStateTransition(context)) {
            doExecute(context);
        }
    }

    static boolean testingStateTransition(StateContext<?, ?> context) {
        return context.getMessageHeader(TESTING_GUARD_KEY) != null;
    }

    static boolean testingStateTransition(Message<?> context) {
        return context.getHeaders().get(TESTING_GUARD_KEY) != null;
    }

    /**
     * Subclasses implement this method, which is executed if we are actually triggering the transition rather than
     * simply testing if it is possible to make the transition.
     */
    protected abstract void doExecute(StateContext<StateType, EventType> context);
}
