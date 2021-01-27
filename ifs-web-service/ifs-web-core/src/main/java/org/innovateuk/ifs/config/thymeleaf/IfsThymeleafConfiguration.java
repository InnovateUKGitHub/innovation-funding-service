package org.innovateuk.ifs.config.thymeleaf;

import org.innovateuk.ifs.config.thymeleaf.dialect.IfSThymeleafDialect;
import org.innovateuk.ifs.config.thymeleaf.postprocessor.IfsThymeleafPostProcessorDialect;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * ThymeleafAutoConfiguration dialect refinements
 * {@link org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration}
 */
@Configuration
@ConditionalOnClass(TemplateMode.class)
@AutoConfigureBefore({ThymeleafAutoConfiguration.class})
public class IfsThymeleafConfiguration {

    @Configuration
    @ConditionalOnClass(IfSThymeleafDialect.class)
    protected static class IfSThymeleafDialectDialectConfiguration {

        @Bean
        @ConditionalOnMissingBean
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
