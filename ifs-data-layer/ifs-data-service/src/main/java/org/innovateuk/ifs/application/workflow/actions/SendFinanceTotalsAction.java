package org.innovateuk.ifs.application.workflow.actions;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationEvent;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.finance.sync.service.FinanceTotalsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

/**
 * This action is intended for updating the application finance totals when it transitions from OPEN to SUBMITTED.
 */
@Component
public class SendFinanceTotalsAction extends BaseApplicationAction {

    @Autowired
    private FinanceTotalsSender financeTotalsSender;

    @Override
    protected void doExecute(final Application application,
                             final StateContext<ApplicationState, ApplicationEvent> context) {
        financeTotalsSender.sendFinanceTotalsForApplication(application.getId());
    }
}