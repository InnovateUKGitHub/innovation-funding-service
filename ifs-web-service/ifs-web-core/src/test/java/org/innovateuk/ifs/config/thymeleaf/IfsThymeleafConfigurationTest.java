package org.innovateuk.ifs.config.thymeleaf;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.innovateuk.ifs.config.thymeleaf.dialect.IfSThymeleafDialect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.dialect.SpringStandardDialect;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"notdebug"})
public class IfsThymeleafConfigurationTest {

    @Autowired
    private ThymeleafProperties thymeleafProperties;

    @Autowired
    private SpringResourceTemplateResolver springResourceTemplateResolver;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    @Test
    public void testContext() {
        assertThat(thymeleafViewResolver.getCharacterEncoding(), equalTo(StandardCharsets.UTF_8.name()));
        assertThat(springTemplateEngine.getEnableSpringELCompiler(), equalTo(true));

        assertThat(springResourceTemplateResolver.getPrefix(), equalTo(ThymeleafProperties.DEFAULT_PREFIX));
        assertThat(springResourceTemplateResolver.getSuffix(), equalTo(ThymeleafProperties.DEFAULT_SUFFIX));
        assertThat(springResourceTemplateResolver.isCacheable(), equalTo(true));
        assertThat(springResourceTemplateResolver.getTemplateMode(), equalTo(TemplateMode.HTML));
    }

    @Test
    public void testAvailableDialects() {
        assertThat(springTemplateEngine.getDialects().size(), equalTo(4));

        // Cross reference expected classes and actual classes
        assertThat(Sets.symmetricDifference(
                springTemplateEngine.getDialects().stream().map(d->d.getClass()).collect(Collectors.toSet()),
                ImmutableSet.of(
                        SpringStandardDialect.class,
                        SpringSecurityDialect.class,
                        IfSThymeleafDialect.class,
                        Java8TimeDialect.class)
        ).size(), equalTo(0));
    }

}