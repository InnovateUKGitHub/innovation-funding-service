package org.innovateuk.ifs.application.workflow.actions;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.innovateuk.ifs.application.resource.ApplicationOutcome;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

/**
 * Action which creates an outcome for the ineligible event.
 * For more info see {@link org.innovateuk.ifs.application.workflow.configuration.ApplicationWorkflow}
 */
@Component
public class MarkIneligibleAction extends BaseApplicationAction {

    @Override
    protected void doExecute(final Application application,
                             final StateContext<ApplicationState, ApplicationOutcome> context) {
        IneligibleOutcome ineligibleOutcome =
                (IneligibleOutcome) context.getMessageHeader("ineligible");

        application.getApplicationProcess().getIneligibleOutcomes().add(ineligibleOutcome);
    }
}
