package org.innovateuk.ifs.starters.audit.cfg.audit;

import org.innovateuk.ifs.starters.audit.AuditAdapter;
import org.innovateuk.ifs.starters.audit.AuditChannel;
import org.innovateuk.ifs.starters.audit.cfg.AuditAutoConfiguration;
import org.innovateuk.ifs.starters.audit.cfg.AuditConfigurationProperties;
import org.innovateuk.ifs.starters.audit.log.LogAuditChannel;
import org.innovateuk.ifs.starters.audit.rabbit.RabbitAuditChannel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.env.AbstractEnvironment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.innovateuk.ifs.IfsProfileConstants.AMQP_PROFILE;
import static org.innovateuk.ifs.starters.audit.cfg.AuditConfigurationProperties.AUDIT_CONFIG_PREFIX;
import static org.innovateuk.ifs.starters.audit.cfg.rabbit.RabbitAuditConfiguration.AUDIT_OBJECT_MAPPER_BEAN_NAME;
import static org.innovateuk.ifs.starters.audit.cfg.rabbit.RabbitAuditConfiguration.AUDIT_QUEUE_BEAN_NAME;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RabbitAuditAutoConfigurationTest {

    @Test
    public void auditContextConfiguration() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withSystemProperties(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME + "=" + AMQP_PROFILE)
            .withPropertyValues(AUDIT_CONFIG_PREFIX + ".auditQueueName=audit",
                    AUDIT_CONFIG_PREFIX + ".auditExchangeName=amqp.fanout")
            .withConfiguration(UserConfigurations.of(AuditConfigurationProperties.class))
            .withConfiguration(AutoConfigurations.of(AuditAutoConfiguration.class, RabbitAutoConfiguration.class));
        contextRunner.run((context) -> {
            assertThat(context.getBean(AuditAdapter.class), is(notNullValue()));
            assertThat(context.getBean(AuditChannel.class), is(notNullValue()));
            assertThat(context.getBean(AUDIT_QUEUE_BEAN_NAME), is(notNullValue()));
            assertThat(context.getBean(RabbitAuditChannel.class), is(notNullValue()));
            assertThat(context.getBean(AUDIT_OBJECT_MAPPER_BEAN_NAME), is(notNullValue()));
            assertThrows(BeansException.class, () -> context.getBean(LogAuditChannel.class));
        });
    }

}