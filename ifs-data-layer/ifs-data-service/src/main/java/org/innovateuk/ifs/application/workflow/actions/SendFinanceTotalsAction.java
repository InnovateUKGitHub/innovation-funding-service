package org.innovateuk.ifs.application.workflow.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationEvent;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.finance.sync.service.FinanceTotalsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

/**
 * This action is intended for updating the application finance totals when it transitions from OPEN to SUBMITTED.
 */
@Component
public class SendFinanceTotalsAction extends BaseApplicationAction {
    private static final Log LOG = LogFactory.getLog(SendFinanceTotalsAction.class);

    @Autowired
    private FinanceTotalsSender financeTotalsSender;

    @Value("${ifs.finance-totals.enabled}")
    private boolean financeTotalsEnabled;

    @Override
    protected void doExecute(final Application application,
                             final StateContext<ApplicationState, ApplicationEvent> context) {
        if(financeTotalsEnabled) {
            LOG.info("Finance totals toggle seems to be enabled. Calling totals sender for applicationId: " + application.getId());
            financeTotalsSender.sendFinanceTotalsForApplication(application.getId());
        }
        else {
            LOG.info("Finance totals toggle seems to be disabled. Not calling totals sender for applicationId: " + application.getId());
        }
    }
}