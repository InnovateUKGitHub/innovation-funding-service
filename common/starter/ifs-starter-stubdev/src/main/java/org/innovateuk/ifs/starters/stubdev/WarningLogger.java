package org.innovateuk.ifs.starters.stubdev;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Final failsafe - log that we are in dev stub mode with devtools running
 */
@Slf4j
public class WarningLogger {

    @Scheduled(fixedDelay = 60000)
    public void repeated() {
        log.error("XX Warning - dev tools and ifs stub mode is running XX");
    }

}
