package org.innovateuk.ifs.config.thymeleaf;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.innovateuk.ifs.config.thymeleaf.dialect.IfSThymeleafDialect;
import org.innovateuk.ifs.config.thymeleaf.postprocessor.IfsThymeleafPostProcessorDialect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@ActiveProfiles({"debug"})
public class IfsThymeleafConfigurationDevTest {

    @Autowired
    private List<IDialect> dialects;

    @Test
    public void testAvailableDialects() {
        assertThat(dialects.size(), equalTo(4));

        // Cross reference expected classes and actual classes
        assertThat(Sets.symmetricDifference(
                dialects.stream().map(d->d.getClass()).collect(Collectors.toSet()),
                ImmutableSet.of(SpringSecurityDialect.class, IfSThymeleafDialect.class, IfsThymeleafPostProcessorDialect.class, Java8TimeDialect.class)
        ).size(), equalTo(0));
    }

}