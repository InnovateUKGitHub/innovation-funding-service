package org.innovateuk.ifs.starters.audit.cfg.log;

import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.starter.common.util.ProfileUtils;
import org.innovateuk.ifs.starters.audit.AuditAdapter;
import org.innovateuk.ifs.starters.audit.AuditChannel;
import org.innovateuk.ifs.starters.audit.cfg.AuditAutoConfiguration;
import org.innovateuk.ifs.starters.audit.cfg.AuditConfigurationProperties;
import org.innovateuk.ifs.starters.audit.log.LogAuditChannel;
import org.innovateuk.ifs.starters.audit.rabbit.RabbitAuditChannel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.innovateuk.ifs.starters.audit.cfg.testcfg.RabbitAuditTestConfiguration.CONTEXT_RESOURCE_LOCK;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LogAuditAutoConfigurationTest {

    @Test
    @ResourceLock(CONTEXT_RESOURCE_LOCK)
    void logContextConfiguration() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withSystemProperties(ProfileUtils.activeProfilesString(IfsProfileConstants.DISABLE_AMQP))
            .withConfiguration(UserConfigurations.of(AuditConfigurationProperties.class))
            .withConfiguration(AutoConfigurations.of(AuditAutoConfiguration.class));
        contextRunner.run((context) -> {
            assertThat(context.getBean(AuditAdapter.class), is(notNullValue()));
            assertThat(context.getBean(AuditChannel.class), is(notNullValue()));
            assertThat(context.getBean(LogAuditChannel.class), is(notNullValue()));
            assertThrows(BeansException.class, () -> context.getBean(RabbitAuditChannel.class));
        });
    }

}