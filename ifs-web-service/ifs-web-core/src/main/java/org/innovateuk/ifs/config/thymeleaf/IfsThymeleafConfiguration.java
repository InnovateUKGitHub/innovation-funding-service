package org.innovateuk.ifs.config.thymeleaf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.checkerframework.checker.units.qual.A;
import org.innovateuk.ifs.config.thymeleaf.dialect.IfSThymeleafDialect;
import org.innovateuk.ifs.config.thymeleaf.postprocessor.IfsThymeleafPostProcessorDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;

import javax.annotation.PostConstruct;

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

    private static final Log logger = LogFactory
            .getLog(IfsThymeleafConfiguration.class);


    @Scheduled(fixedDelay = 10000L)
    public void log() {
        if (springTemplateEngine == null) {
            logger.error("null springTemplateEngine");
        } else {
            if (springTemplateEngine.getDialects() == null) {
                logger.error("null getDialects");
            } else {
                for (IDialect dialect : springTemplateEngine.getDialects()) {
                    logger.error("dialect " + dialect.getClass());
                    logger.error("dialect " + dialect.getName());
                }
            }
        }
    }

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
