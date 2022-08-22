package org.innovateuk.ifs.starters.messaging.cfg;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.innovateuk.ifs.starters.messaging.cfg.MessagingConfigurationProperties.MESSAGING_CONFIG_PREFIX;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = MESSAGING_CONFIG_PREFIX)
public class MessagingConfigurationProperties {

    public static final String MESSAGING_CONFIG_PREFIX = "ifs.starter.messaging";

}
