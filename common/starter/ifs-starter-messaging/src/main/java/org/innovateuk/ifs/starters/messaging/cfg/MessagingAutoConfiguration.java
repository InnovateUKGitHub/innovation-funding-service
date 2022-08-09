package org.innovateuk.ifs.starters.messaging.cfg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(MessagingConfigurationProperties.class)
@AutoConfigureAfter(RabbitAutoConfiguration.class)
public class MessagingAutoConfiguration {

    @Autowired
    private MessagingConfigurationProperties messagingConfigurationProperties;


}
