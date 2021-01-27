package org.innovateuk.ifs.config.thymeleaf;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.innovateuk.ifs.config.thymeleaf.dialect.IfSThymeleafDialect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@RunWith(SpringRunner.class)
@EnableAutoConfiguration
public class IfsThymeleafConfigurationTest {

    @Autowired
    private List<IDialect> dialects;

    @Autowired
    private SpringResourceTemplateResolver springResourceTemplateResolver;

    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    @Test
    public void testAvailableDialects() {
        assertThat(dialects.size(), equalTo(3));

        // Cross reference expected classes and actual classes
        assertThat(Sets.symmetricDifference(
                dialects.stream().map(d->d.getClass()).collect(Collectors.toSet()),
                ImmutableSet.of(SpringSecurityDialect.class, IfSThymeleafDialect.class, Java8TimeDialect.class)
        ).size(), equalTo(0));
    }

}