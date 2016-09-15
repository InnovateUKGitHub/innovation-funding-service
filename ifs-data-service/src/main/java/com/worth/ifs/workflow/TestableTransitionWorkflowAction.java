package com.worth.ifs.workflow;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * TODO DW - document this class
 */
public abstract class TestableTransitionWorkflowAction implements Action<String, String> {

    public static final String TESTING_GUARD_KEY = "com.worth.ifs.workflow__TESTING_GUARD_KEY__";

    @Override
    public final void execute(StateContext<String, String> context) {
        if (context.getMessageHeader(TESTING_GUARD_KEY) == null) {
            doExecute(context);
        }
    }

    protected abstract void doExecute(StateContext<String, String> context);
}
