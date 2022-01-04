package org.innovateuk.ifs.config.thymeleaf;

import org.innovateuk.ifs.config.thymeleaf.dialect.IfSThymeleafDialect;
import org.innovateuk.ifs.config.thymeleaf.postprocessor.IfsThymeleafPostProcessorDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * ThymeleafAutoConfiguration dialect refinements
 * {@link org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration}
 */
@Configuration
@ConditionalOnClass(TemplateMode.class)
@EnableScheduling
@AutoConfigureBefore({ThymeleafAutoConfiguration.class})
public class IfsThymeleafConfiguration {

    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    @Configuration
    protected static class IfSThymeleafDialectDialectConfiguration {

        @Bean
        public IDialect ifSThymeleafDialect() {
            return new IfSThymeleafDialect();
        }
    }

    @Configuration
    @Profile("debug")
    protected static class IfsThymeleafPostProcessorDialectConfiguration {

        @Bean
        public IDialect ifsThymeleafPostProcessorDialect() {
            return new IfsThymeleafPostProcessorDialect();
        }
    }

}
