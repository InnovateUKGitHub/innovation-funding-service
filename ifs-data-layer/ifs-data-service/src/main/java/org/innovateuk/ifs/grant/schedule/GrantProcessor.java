package org.innovateuk.ifs.grant.schedule;

import org.innovateuk.ifs.grant.service.GrantService;
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
public class GrantProcessor {
    @Autowired
    private GrantService service;

    @Autowired
    private ScheduleStatusService scheduleStatusService;

    private static final String JOB_NAME = "GRANT_SEND";

    @Scheduled(fixedDelayString = "${ifs.data.service.file.grant.send.delay.millis:60000}")
    public void send() {
        try {
            scheduleStatusService.startJob(JOB_NAME);
        } catch (Exception e) {
            return;
        }
        service.sendReadyProjects();
        scheduleStatusService.endJob(JOB_NAME);
    }
}
