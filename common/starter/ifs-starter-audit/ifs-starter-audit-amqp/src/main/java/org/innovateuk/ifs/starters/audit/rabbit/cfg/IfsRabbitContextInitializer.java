package org.innovateuk.ifs.starters.audit.rabbit.cfg;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;

import static org.innovateuk.ifs.IfsProfileConstants.REDIS_STANDALONE_CACHE;
import static org.innovateuk.ifs.IfsProfileConstants.SIMPLE_CACHE;
import static org.innovateuk.ifs.starter.common.util.ProfileUtils.profileMatches;
import static org.innovateuk.ifs.starter.common.util.YamlPropertyLoader.registerPropertySource;

/**
 * Adds startup settings to set cache modes, SIMPLE, REDIS and REDIS CLUSTER
 */
public class IfsRabbitContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String AUTO_CFG_RABBIT_YML = "autoconfig-amqp-rabbit.yml";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        registerPropertySource(applicationContext, new ClassPathResource(AUTO_CFG_RABBIT_YML));
    }

}
