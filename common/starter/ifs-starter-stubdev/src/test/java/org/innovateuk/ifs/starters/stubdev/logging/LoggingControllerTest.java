package org.innovateuk.ifs.starters.stubdev.logging;

import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.starters.stubdev.cfg.StubDevConfigurationProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.logging.LoggersEndpoint;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.innovateuk.ifs.starters.stubdev.logging.LoggingController.THYMELEAF_LOG_KEY;

@ExtendWith(SpringExtension.class)
@ActiveProfiles({IfsProfileConstants.STUBDEV})
@SpringBootTest(classes = {LoggingController.class, LoggersEndpoint.class})
@EnableConfigurationProperties(StubDevConfigurationProperties.class)
class LoggingControllerTest {

    @Autowired
    private LoggingController loggingController;

    @Autowired
    private LoggersEndpoint loggersEndpoint;

    @Test
    void logLevel() {
        assertThat(loggersEndpoint.loggerLevels("foo"), is(nullValue()));
        loggingController.logLevel("foo", LogLevel.WARN.name());
        assertThat(loggersEndpoint.loggerLevels("foo").getConfiguredLevel(), equalTo(LogLevel.WARN.name()));
    }

    @Test
    void thymeleaf() {
        loggingController.logLevel(THYMELEAF_LOG_KEY, LogLevel.ERROR.name());
        assertThat(loggersEndpoint.loggerLevels(THYMELEAF_LOG_KEY).getConfiguredLevel(), equalTo(LogLevel.ERROR.name()));
        loggingController.thymeleafTrace();
        assertThat(loggersEndpoint.loggerLevels(THYMELEAF_LOG_KEY).getConfiguredLevel(), equalTo(LogLevel.TRACE.name()));
    }

    @Test
    void thymeleafInfo() {
        loggingController.logLevel(THYMELEAF_LOG_KEY, LogLevel.ERROR.name());
        assertThat(loggersEndpoint.loggerLevels(THYMELEAF_LOG_KEY).getConfiguredLevel(), equalTo(LogLevel.ERROR.name()));
        loggingController.thymeleafInfo();
        assertThat(loggersEndpoint.loggerLevels(THYMELEAF_LOG_KEY).getConfiguredLevel(), equalTo(LogLevel.INFO.name()));
    }
}