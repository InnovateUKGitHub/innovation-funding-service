package org.innovateuk.ifs.application.workflow.actions;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationEvent;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

/**
 * This action is intended for setting start date on always open ktp competitions when the application transitions
 * from OPEN to SUBMITTED.
 */
@Component
public class AlwaysOpenKtpStartDateAction extends BaseApplicationAction {
    private static final Logger LOG = LoggerFactory.getLogger(AlwaysOpenKtpStartDateAction.class);

    @Override
    protected void doExecute(final Application application,
                             final StateContext<ApplicationState, ApplicationEvent> context) {
        if (application.getStartDate() == null &&
                application.getCompetition().isAlwaysOpen() &&
                application.getCompetition().isKtp()) {
             application.setStartDate(TimeZoneUtil.toUkTimeZone(ZonedDateTime.now()).plusMonths(12).toLocalDate());
         }
    }
}