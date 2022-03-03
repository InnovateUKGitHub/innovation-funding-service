package org.innovateuk.ifs.starters.stubdev.cfg;

import org.innovateuk.ifs.IfsProfileConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Arrays;

public class StubDevPropertiesPostProcessor implements EnvironmentPostProcessor {

    public static final String AUTOCONFIG_STUBDEV_YML = "autoconfig-stubdev.yml";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (Arrays.stream(environment.getActiveProfiles()).anyMatch(s -> s.equals(IfsProfileConstants.STUBDEV))) {
            Resource resource = new ClassPathResource(AUTOCONFIG_STUBDEV_YML);
            if (resource.exists()) {
                registerPropertySource(environment, resource);
            }
        }
    }

    protected void registerPropertySource(ConfigurableEnvironment environment, Resource resource) {
        PropertySourceLoader loader = new YamlPropertySourceLoader();
        try {
            OriginTrackedMapPropertySource propertyFileSource = (OriginTrackedMapPropertySource) loader
                    .load(AUTOCONFIG_STUBDEV_YML, resource).get(0);
            environment.getPropertySources().addFirst(propertyFileSource);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load properties from " + resource, ex);
        }
    }
}
