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

import static org.innovateuk.ifs.starter.common.util.ProfileUtils.profileMatches;
import static org.innovateuk.ifs.starter.common.util.YamlPropertyLoader.registerPropertySource;

public class StubDevPropertiesPostProcessor implements EnvironmentPostProcessor {

    public static final String AUTOCONFIG_STUBDEV_YML = "autoconfig-stubdev.yml";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (profileMatches(environment, IfsProfileConstants.STUBDEV)) {
            registerPropertySource(environment, new ClassPathResource(AUTOCONFIG_STUBDEV_YML));
        }
    }

}
