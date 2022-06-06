package org.innovateuk.ifs.starters.audit.cfg.log;

import org.innovateuk.ifs.api.audit.AuditChannel;
import org.innovateuk.ifs.starters.audit.AuditAdapter;
import org.innovateuk.ifs.starters.audit.log.LogAuditChannel;
import org.innovateuk.ifs.starters.audit.log.cfg.AuditAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class LogAuditAutoConfigurationTest {

    @Test
    void logContextConfiguration() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AuditAutoConfiguration.class));
        contextRunner.run((context) -> {
            assertThat(context.getBean(AuditAdapter.class), is(notNullValue()));
            assertThat(context.getBean(AuditChannel.class), is(notNullValue()));
            assertThat(context.getBean(LogAuditChannel.class), is(notNullValue()));
        });
    }

}