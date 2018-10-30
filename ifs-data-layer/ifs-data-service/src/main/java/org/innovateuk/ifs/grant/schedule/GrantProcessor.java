package org.innovateuk.ifs.grant.schedule;

import org.innovateuk.ifs.grant.service.GrantService;
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

    @Scheduled(fixedDelayString = "${ifs.data.service.file.grant.send.delay.millis:10000}")
    public void send() {
        service.sendReadyProjects();
    }
}
