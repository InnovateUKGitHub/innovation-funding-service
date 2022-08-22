package org.innovateuk.ifs.starters.audit.cfg.rabbit;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;

import static org.innovateuk.ifs.starter.common.util.YamlPropertyLoader.registerPropertySource;

/**
 * Adds startup settings for rabbit
 */
public class IfsRabbitContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String AUTO_CFG_RABBIT_YML = "autoconfig-amqp-rabbit.yml";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        registerPropertySource(applicationContext, new ClassPathResource(AUTO_CFG_RABBIT_YML));
    }

}
