package org.innovateuk.ifs.application.workflow.actions;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationEvent;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.springframework.statemachine.StateContext;

/**
 * A base class for Application-related workflow actions.
 */
public abstract class BaseApplicationAction extends TestableTransitionWorkflowAction<ApplicationState, ApplicationEvent> {

    @Override
    protected void doExecute(final StateContext<ApplicationState, ApplicationEvent> context) {
        Application application = getApplicationFromContext(context);
        doExecute(application, context);
    }

    private Application getApplicationFromContext(StateContext<ApplicationState, ApplicationEvent> context) {
        return (Application) context.getMessageHeader("target");
    }

    protected abstract void doExecute(Application application, StateContext<ApplicationState, ApplicationEvent> context);

}
