package org.innovateuk.ifs.starters.feign.cfg;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;

import static org.innovateuk.ifs.starter.common.util.YamlPropertyLoader.registerPropertySource;

public class IfsFeignConfigContextInitializer implements ApplicationContextInitializer {

    private static final String FEIGN_CONFIG = "autoconfig-feign.yml";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        registerPropertySource(applicationContext, new ClassPathResource(FEIGN_CONFIG));
    }

}
