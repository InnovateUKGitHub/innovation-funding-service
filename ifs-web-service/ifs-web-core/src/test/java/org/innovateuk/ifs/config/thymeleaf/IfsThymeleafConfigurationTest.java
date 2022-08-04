package org.innovateuk.ifs.config.thymeleaf;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.innovateuk.ifs.config.thymeleaf.dialect.IfSThymeleafDialect;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.dialect.SpringStandardDialect;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class IfsThymeleafConfigurationTest {

    @Test
    public void testAutoConfiguration() {
        new ApplicationContextRunner()
            .withConfiguration(
                    AutoConfigurations.of(IfsThymeleafConfiguration.class, ThymeleafAutoConfiguration.class)
            ).withSystemProperties(
                    "spring.thymeleaf.enable-spring-el-compiler=true"
            ).run((context) -> {
                assertThat(context.getBean(SpringResourceTemplateResolver.class).getCharacterEncoding(), equalTo(StandardCharsets.UTF_8.name()));
                assertThat(context.getBean(SpringTemplateEngine.class).getEnableSpringELCompiler(), equalTo(true));
                assertThat(context.getBean(SpringResourceTemplateResolver.class).getPrefix(), equalTo(ThymeleafProperties.DEFAULT_PREFIX));
                assertThat(context.getBean(SpringResourceTemplateResolver.class).getSuffix(), equalTo(ThymeleafProperties.DEFAULT_SUFFIX));
                assertThat(context.getBean(SpringResourceTemplateResolver.class).isCacheable(), equalTo(true));
                assertThat(context.getBean(SpringResourceTemplateResolver.class).getTemplateMode(), equalTo(TemplateMode.HTML));

                assertThat(context.getBean(SpringTemplateEngine.class).getDialects().size(), equalTo(4));
                // Cross reference expected classes and actual classes
                assertThat(Sets.symmetricDifference(
                        context.getBean(SpringTemplateEngine.class).getDialects().stream().map(d->d.getClass()).collect(Collectors.toSet()),
                        ImmutableSet.of(
                                SpringStandardDialect.class,
                                SpringSecurityDialect.class,
                                IfSThymeleafDialect.class,
                                Java8TimeDialect.class)
                ).size(), equalTo(0));
            });
    }

}