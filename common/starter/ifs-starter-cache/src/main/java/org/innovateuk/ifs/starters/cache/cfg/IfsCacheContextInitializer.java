package org.innovateuk.ifs.starters.cache.cfg;

import com.google.common.collect.ImmutableList;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

import static org.innovateuk.ifs.IfsProfileConstants.REDIS_STANDALONE_CACHE;
import static org.innovateuk.ifs.IfsProfileConstants.SIMPLE_CACHE;

/**
 * Adds startup settings to set cache modes, SIMPLE, REDIS and REDIS CLUSTER
 */
public class IfsCacheContextInitializer implements ApplicationContextInitializer {

    private static final String SIMPLE_CACHE_YML = "autoconfig-simple-cache.yml";
    private static final String REDIS_STANDALONE_CACHE_YML = "autoconfig-redis-cache.yml";
    private static final String REDIS_CLUSTER_CACHE_YML = "autoconfig-redis-cluster-cache.yml";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (matchProfile(applicationContext.getEnvironment(), ImmutableList.of(SIMPLE_CACHE))) {
            registerPropertySource(applicationContext.getEnvironment(), new ClassPathResource(SIMPLE_CACHE_YML));
        } else if (matchProfile(applicationContext.getEnvironment(), ImmutableList.of(REDIS_STANDALONE_CACHE))) {
            registerPropertySource(applicationContext.getEnvironment(), new ClassPathResource(REDIS_STANDALONE_CACHE_YML));
        } else {
            registerPropertySource(applicationContext.getEnvironment(), new ClassPathResource(REDIS_CLUSTER_CACHE_YML));
        }
    }

    private boolean matchProfile(ConfigurableEnvironment environment, List<String> profiles) {
        for (String profile : environment.getActiveProfiles()) {
            if (profiles.contains(profile)) {
                return true;
            }
        }
        return false;
    }

    protected void registerPropertySource(ConfigurableEnvironment environment, Resource resource) {
        PropertySourceLoader loader = new YamlPropertySourceLoader();
        try {
            OriginTrackedMapPropertySource propertyFileSource = (OriginTrackedMapPropertySource) loader
                    .load(IfsCacheContextInitializer.class.getSimpleName(), resource).get(0);
            environment.getPropertySources().addFirst(propertyFileSource);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load properties from " + resource, ex);
        }
    }

}
