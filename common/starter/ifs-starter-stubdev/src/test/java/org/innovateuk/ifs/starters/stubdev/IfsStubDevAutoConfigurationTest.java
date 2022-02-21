package org.innovateuk.ifs.starters.stubdev;

import com.google.common.collect.ImmutableList;
import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.starters.stubdev.cfg.StubDevConfigurationProperties;
import org.innovateuk.ifs.starters.stubdev.filter.RewriteFilter;
import org.innovateuk.ifs.starters.stubdev.security.StubUidSupplier;
import org.innovateuk.ifs.starters.stubdev.thymeleaf.IfsThymeleafPostProcessorDialect;
import org.innovateuk.ifs.starters.stubdev.util.WarningLogger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.devtools.autoconfigure.LocalDevToolsAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.innovateuk.ifs.starters.stubdev.Constants.STUB_DEV_PROPS_PREFIX;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IfsStubDevAutoConfigurationTest {

    private static final List COMMON = ImmutableList.of(
            WarningLogger.class,
            StubUidSupplier.class,
            ITemplateResolver.class,
            RewriteFilter.class,
            StubDevConfigurationProperties.class
    );

    private static final String PROFILE_PROP = AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME + "=" + IfsProfileConstants.STUBDEV;

    @Test
    public void testConfigWithDevtoolsAndProfileAndThymleafDebug() {
        new ApplicationContextRunner()
                .withSystemProperties(PROFILE_PROP)
                .withSystemProperties(STUB_DEV_PROPS_PREFIX + ".validateHtml=true")
                .withConfiguration(
                        AutoConfigurations.of(LocalDevToolsAutoConfiguration.class, IfsStubDevAutoConfiguration.class)
                ).run((context) -> {
                    assertFound(context, COMMON);
                    assertFound(context, ImmutableList.of(IfsThymeleafPostProcessorDialect.class));
                });
    }

    @Test
    public void testConfigWithDevtoolsAndProfile() {
        new ApplicationContextRunner()
                .withSystemProperties(PROFILE_PROP)
                .withConfiguration(
                        AutoConfigurations.of(LocalDevToolsAutoConfiguration.class, IfsStubDevAutoConfiguration.class)
                ).run((context) -> {
                    assertFound(context, COMMON);
                    assertNotFound(context, ImmutableList.of(IfsThymeleafPostProcessorDialect.class));
                });
    }

    @Test
    public void testConfigWithDevtoolsNoProfile() {
        new ApplicationContextRunner()
                .withConfiguration(
                        AutoConfigurations.of(LocalDevToolsAutoConfiguration.class, IfsStubDevAutoConfiguration.class)
                ).run((context) -> {
                    assertNotFound(context, COMMON);
                    assertNotFound(context, ImmutableList.of(IfsThymeleafPostProcessorDialect.class));
                });
    }

    @Test
    public void testConfigNoDevtoolsNoProfile() {
        new ApplicationContextRunner()
                .withConfiguration(
                        AutoConfigurations.of(IfsStubDevAutoConfiguration.class)
                ).run((context) -> {
                    assertNotFound(context, COMMON);
                    assertNotFound(context, ImmutableList.of(IfsThymeleafPostProcessorDialect.class));
                });
    }

    private static void assertNotFound(ApplicationContext context, List<Class> clzs) {
        clzs.stream().forEach(clz -> assertThrows(BeansException.class, () -> context.getBean(clz)));
    }

    private static void assertFound(ApplicationContext context, List<Class> clzs) {
        clzs.stream().forEach(clz -> assertThat(context.getBean(clz), is(notNullValue())));
    }
}