package org.innovateuk.ifs.grant.schedule;

import org.innovateuk.ifs.grant.service.GrantService;
import org.innovateuk.ifs.schedule.transactional.ScheduleStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Processor that sends projects data to grant service.
 */
@Component
public class GrantProcessor {
    @Autowired
    private GrantService service;

    @Autowired
    private ScheduleStatusService scheduleStatusService;

    private static final String JOB_NAME = "GRANT_SEND";

    @Scheduled(fixedDelayString = "${ifs.data.service.file.grant.send.delay.millis:10000}")
    public void send() {
        scheduleStatusService.startScheduledJob(JOB_NAME);
        service.sendReadyProjects();
        scheduleStatusService.endScheduledJob(JOB_NAME);
    }
}
