package org.innovateuk.ifs.profile.schedule;

import org.innovateuk.ifs.profile.transactional.DoiExpiryService;
import org.innovateuk.ifs.schedule.transactional.ScheduleStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Processor that sends projects data to grant service.
 */
@Component
@Profile("!integration-test")
public class DoiExpiryNotificationProcessor {
    @Autowired
    private DoiExpiryService service;

    @Autowired
    private ScheduleStatusService scheduleStatusService;

    private static final String JOB_NAME = "ASSESSOR_DOI_EXPIRY";

    @Scheduled(fixedDelayString = "60000")
    public void send() {
        try {
            scheduleStatusService.startJob(JOB_NAME);
        } catch (Exception e) {
            return;
        }
        service.notifyExpiredDoi();
        scheduleStatusService.endJob(JOB_NAME);
    }
}
