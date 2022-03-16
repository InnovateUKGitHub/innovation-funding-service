package org.innovateuk.ifs.starters.cache.cfg;

import org.innovateuk.ifs.starter.common.util.YamlPropertyLoader;
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
public class IfsCacheContextInitializer implements ApplicationContextInitializer {

    private static final String SIMPLE_CACHE_YML = "autoconfig-simple-cache.yml";
    private static final String REDIS_STANDALONE_CACHE_YML = "autoconfig-redis-cache.yml";
    private static final String REDIS_CLUSTER_CACHE_YML = "autoconfig-redis-cluster-cache.yml";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (profileMatches(applicationContext, SIMPLE_CACHE)) {
            registerPropertySource(applicationContext, new ClassPathResource(SIMPLE_CACHE_YML));
        } else if (profileMatches(applicationContext, REDIS_STANDALONE_CACHE)) {
            registerPropertySource(applicationContext, new ClassPathResource(REDIS_STANDALONE_CACHE_YML));
        } else {
            registerPropertySource(applicationContext, new ClassPathResource(REDIS_CLUSTER_CACHE_YML));
        }
    }

}
