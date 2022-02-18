package org.innovateuk.ifs.starters.stubdev.logging;

import org.innovateuk.ifs.starters.stubdev.cfg.StubDevConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.logging.LoggersEndpoint;
import org.springframework.boot.logging.LogLevel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * Convenience wrapper for setting log levels
 *
 * Technically they should be PUT, but its more convenient this way.
 */
@RestController
public class LoggingController {

    public static final String THYMELEAF_LOG_KEY = "org.thymeleaf.engine";

    public static final String PATH_PREFIX = "/logging/";

    @Autowired
    private LoggersEndpoint loggersEndpoint;

    @Autowired
    private StubDevConfigurationProperties stubDevConfigurationProperties;

    @PostConstruct
    public void init() {
        if (stubDevConfigurationProperties.isLogThymeLeafTemplates()) {
            thymeleafTrace();
        }
    }

    @GetMapping(value = PATH_PREFIX + "{logger}/{level}", produces = TEXT_PLAIN_VALUE)
    public String logLevel(@PathVariable String logger, @PathVariable String level) {
        loggersEndpoint.configureLogLevel(logger, LogLevel.valueOf(level));
        return logger + "/" + level;
    }

    @GetMapping(value = PATH_PREFIX + "thymeleafTrace", produces = TEXT_PLAIN_VALUE)
    public String thymeleafTrace() {
        loggersEndpoint.configureLogLevel(THYMELEAF_LOG_KEY, LogLevel.TRACE);
        return "thymeLeaf/TRACE";
    }

    @GetMapping(value = PATH_PREFIX + "thymeleafInfo", produces = TEXT_PLAIN_VALUE)
    public String thymeleafInfo() {
        loggersEndpoint.configureLogLevel(THYMELEAF_LOG_KEY, LogLevel.INFO);
        return "thymeLeaf/INFO";
    }

}
