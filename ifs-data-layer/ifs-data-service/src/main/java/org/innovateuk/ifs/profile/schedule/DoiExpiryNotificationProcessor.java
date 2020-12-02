package org.innovateuk.ifs.profile.schedule;

import org.innovateuk.ifs.profile.transactional.DoiExpiryService;
import org.innovateuk.ifs.schedule.transactional.ScheduleStatusWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Processor that checks for expired DOIs.
 */
@Component
@Profile("!integration-test")
public class DoiExpiryNotificationProcessor {
    @Autowired
    private DoiExpiryService service;

    @Autowired
    private ScheduleStatusWrapper scheduleStatusWrapper;

    private static final String JOB_NAME = "ASSESSOR_DOI_EXPIRY";

    @Scheduled(fixedDelayString = "60000")
    public void send() {
        scheduleStatusWrapper.doScheduledJob(JOB_NAME, () -> service.notifyExpiredDoi());
    }
}
