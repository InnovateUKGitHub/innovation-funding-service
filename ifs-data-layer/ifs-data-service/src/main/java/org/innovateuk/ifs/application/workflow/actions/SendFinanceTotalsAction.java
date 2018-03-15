package org.innovateuk.ifs.application.workflow.actions;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationEvent;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.finance.totals.service.ApplicationFinanceTotalsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

/**
 * This action is intended for updating the application finance totals when it transitions from OPEN to SUBMITTED.
 */
@Component
public class SendFinanceTotalsAction extends BaseApplicationAction {
    private static final Logger LOG = LoggerFactory.getLogger(SendFinanceTotalsAction.class);

    @Autowired
    private ApplicationFinanceTotalsSender applicationFinanceTotalsSender;

    @Value("${ifs.finance-totals.enabled}")
    private boolean financeTotalsEnabled;

    @Override
    protected void doExecute(final Application application,
                             final StateContext<ApplicationState, ApplicationEvent> context) {
        if(financeTotalsEnabled) {
            LOG.info("Calling totals sender for applicationId: {}", application.getId());
            applicationFinanceTotalsSender.sendFinanceTotalsForApplication(application.getId());
        }
        else {
            LOG.info("Not calling totals sender for applicationId: {}", application.getId());
        }
    }
}