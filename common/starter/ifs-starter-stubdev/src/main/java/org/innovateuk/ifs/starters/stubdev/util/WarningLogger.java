package org.innovateuk.ifs.starters.stubdev.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Log that we are in dev stub mode with devtools running.
 */
@Slf4j
public class WarningLogger {

    @Scheduled(initialDelay = 500, fixedDelay = 60000)
    public void repeated() {
        log.error("XX Dev tools and ifs stub mode is running XX");
    }

}
