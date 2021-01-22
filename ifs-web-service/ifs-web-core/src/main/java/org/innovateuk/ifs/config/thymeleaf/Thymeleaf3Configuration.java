package org.innovateuk.ifs.config.thymeleaf;

import org.innovateuk.ifs.config.thymeleaf.dialect.IfSThymeleafDialect;
import org.innovateuk.ifs.config.thymeleaf.postprocessor.IfsThymeleafPostProcessorDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.core.parameters.P;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

/**
 * Configuration for Thymeleaf 3 to replace the auto-configuration that was available for Thymeleaf 2 in
 * {@link org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration}
 */
@Configuration
@ConditionalOnClass({SpringTemplateEngine.class})
@EnableConfigurationProperties({ThymeleafProperties.class})
@AutoConfigureAfter({WebMvcAutoConfiguration.class})
public class Thymeleaf3Configuration implements WebMvcConfigurer {

    @Autowired
    protected Environment env;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    @Bean
    public ISpringTemplateEngine templateEngine() {

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver());
        engine.setEnableSpringELCompiler(true);
        engine.addDialect(new Java8TimeDialect());
        engine.addDialect(new IfSThymeleafDialect());

//        if (env.acceptsProfiles("debug")) {
//            engine.addDialect(new IfsThymeleafPostProcessorDialect());
//        }

        engine.addDialect(new SpringSecurityDialect());
        return engine;
    }

    @Configuration
    @Profile("debug")
    static class IfsThymeleafPostProcessorDialectConfiguration {
        @Bean
        public IDialect ifsThymeleafPostProcessorDialect() {
            return new IfsThymeleafPostProcessorDialect();
        }
    }

}
