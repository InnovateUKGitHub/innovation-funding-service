package org.innovateuk.ifs.starters.cache.cfg;

import com.google.common.collect.ImmutableList;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

import static org.innovateuk.ifs.IfsProfileConstants.NOCACHE;

/**
 * Adds startup settings to disable redis caching when in NOCACHE profile
 */
public class SimpleCachePropertiesPostProcessor implements EnvironmentPostProcessor {

    private static final List PROFILES = ImmutableList.of(NOCACHE);

    public static final String CACHE_YML = "autoconfig-cache.yml";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (matchProfile(environment)) {
            Resource resource = new ClassPathResource(CACHE_YML);
            registerPropertySource(environment, resource);
        }
    }

    private boolean matchProfile(ConfigurableEnvironment environment) {
        for (String profile : environment.getActiveProfiles()) {
            if (PROFILES.contains(profile)) {
                return true;
            }
        }
        return false;
    }

    protected void registerPropertySource(ConfigurableEnvironment environment, Resource resource) {
        PropertySourceLoader loader = new YamlPropertySourceLoader();
        try {
            OriginTrackedMapPropertySource propertyFileSource = (OriginTrackedMapPropertySource) loader
                    .load(CACHE_YML, resource).get(0);
            environment.getPropertySources().addFirst(propertyFileSource);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load properties from " + resource, ex);
        }
    }
}
