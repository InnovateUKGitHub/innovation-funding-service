package org.innovateuk.ifs.starter.common.util;

import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;

import java.io.IOException;

public class YamlPropertyLoader {

    /**
     * Loads yaml properties into the spring context as a property source.
     * @param environment the spring ConfigurableEnvironment
     * @param resource the yaml file
     */
    public static void registerPropertySource(ConfigurableEnvironment environment, Resource resource) {
        PropertySourceLoader loader = new YamlPropertySourceLoader();
        try {
            OriginTrackedMapPropertySource propertyFileSource = (OriginTrackedMapPropertySource) loader
                    .load(YamlPropertyLoader.class.getSimpleName() + "->" + resource.getFilename(), resource).get(0);
            environment.getPropertySources().addFirst(propertyFileSource);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load properties from " + resource, ex);
        }
    }

    public static void registerPropertySource(ConfigurableApplicationContext context, Resource resource) {
        registerPropertySource(context.getEnvironment(), resource);
    }

}
