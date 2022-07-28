package org.innovateuk.ifs.starters.messaging.cfg;

import org.innovateuk.ifs.starter.common.util.ProfileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListenerAnnotationBeanPostProcessor;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class MessagingAutoConfigurationTest {

    @Test
    void messageContextConfiguration() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withSystemProperties("IFS_RABBIT_PASSWORD=setAsEnvVar")
            .withConfiguration(UserConfigurations.of(MessagingAutoConfiguration.class))
            .withConfiguration(AutoConfigurations.of(RabbitAutoConfiguration.class))
            .withInitializer(new MessagingContextInitializer());
        contextRunner.run((context) -> {
            // FYI RabbitTemplate and AmqpTemplate are the same bean
            assertThat(context.getBean(RabbitTemplate.class), is(notNullValue()));
            assertThat(context.getBean(AmqpTemplate.class), is(notNullValue()));
            assertThat(context.getBean(MessagingConfigurationProperties.class), is(notNullValue()));
            assertThat(context.getBean(CachingConnectionFactory.class), is(notNullValue()));
            assertThat(context.getBean(AmqpAdmin.class), is(notNullValue()));
            assertThat(context.getBean(RabbitMessagingTemplate.class), is(notNullValue()));
            assertThat(context.getBean(RabbitListenerAnnotationBeanPostProcessor.class), is(notNullValue()));
            assertThat(context.getBean(RabbitProperties.class), is(notNullValue()));
            assertThat(context.getBean(RabbitProperties.class).getUsername(), equalTo("guest"));
            assertThat(context.getBean(RabbitProperties.class).getPassword(), equalTo("setAsEnvVar"));
            assertThat(context.getBean(RabbitProperties.class).getListener().getSimple().getPrefetch(), equalTo(1));
        });
    }

}