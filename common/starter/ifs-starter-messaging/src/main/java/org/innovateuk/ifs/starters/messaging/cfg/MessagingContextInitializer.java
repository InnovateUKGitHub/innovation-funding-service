package org.innovateuk.ifs.starters.messaging.cfg;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;

import static org.innovateuk.ifs.IfsProfileConstants.AMQP_PROFILE;
import static org.innovateuk.ifs.starter.common.util.ProfileUtils.profileMatches;
import static org.innovateuk.ifs.starter.common.util.YamlPropertyLoader.registerPropertySource;

public class MessagingContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String AUTO_CFG_RABBIT_YML = "autoconfig-messaging.yml";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (profileMatches(applicationContext, AMQP_PROFILE)) {
            registerPropertySource(applicationContext, new ClassPathResource(AUTO_CFG_RABBIT_YML));
        }
    }
}