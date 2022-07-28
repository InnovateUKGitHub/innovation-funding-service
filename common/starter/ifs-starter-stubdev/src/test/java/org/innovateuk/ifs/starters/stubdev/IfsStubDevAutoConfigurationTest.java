package org.innovateuk.ifs.starters.stubdev;

import com.google.common.collect.ImmutableList;
import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.starter.common.util.ProfileUtils;
import org.innovateuk.ifs.starters.stubdev.cfg.StubDevConfigurationProperties;
import org.innovateuk.ifs.starters.stubdev.filter.RewriteFilter;
import org.innovateuk.ifs.starters.stubdev.security.StubUidSupplier;
import org.innovateuk.ifs.starters.stubdev.thymeleaf.IfsThymeleafPostProcessorDialect;
import org.innovateuk.ifs.starters.stubdev.util.TimerAspect;
import org.innovateuk.ifs.starters.stubdev.util.WarningLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.devtools.autoconfigure.LocalDevToolsAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.innovateuk.ifs.IfsProfileConstants.STUBDEV;
import static org.innovateuk.ifs.starters.stubdev.cfg.StubDevConfigurationProperties.STUB_DEV_PROPS_PREFIX;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IfsStubDevAutoConfigurationTest {

    private static final List COMMON = ImmutableList.of(
            WarningLogger.class,
            StubUidSupplier.class,
            ITemplateResolver.class,
            RewriteFilter.class,
            StubDevConfigurationProperties.class,
            IfsStubDevAutoConfiguration.class
    );

    // Require properties to be set
    private static final List UNCOMMON = ImmutableList.of(
            IfsThymeleafPostProcessorDialect.class,
            TimerAspect.class
    );

    @Test
    @ResourceLock("COMMON")
    void testConfigWithDevtoolsAndProfileAndThymleafDebug() {
        new ApplicationContextRunner()
                .withSystemProperties(ProfileUtils.activeProfilesString(STUBDEV))
                .withSystemProperties(
                        STUB_DEV_PROPS_PREFIX + ".validateHtml=true",
                        STUB_DEV_PROPS_PREFIX + ".enableClientMethodTiming=true",
                        STUB_DEV_PROPS_PREFIX + ".logThymeLeafTemplates=true",
                        STUB_DEV_PROPS_PREFIX + ".defaultUuid=123-456-789"
                )
                .withConfiguration(
                        AutoConfigurations.of(LocalDevToolsAutoConfiguration.class, IfsStubDevAutoConfiguration.class)
                ).withBean(TimerAspect.class).run((context) -> {
                    assertFound(context, COMMON);
                    assertFound(context, UNCOMMON);
                });
    }

    @Test
    @ResourceLock("COMMON")
    void testConfigWithDevtoolsAndWrongProfile() {
        new ApplicationContextRunner()
                .withSystemProperties(ProfileUtils.activeProfilesString(IfsProfileConstants.LOCAL_STORAGE))
                .withConfiguration(
                        AutoConfigurations.of(LocalDevToolsAutoConfiguration.class, IfsStubDevAutoConfiguration.class)
                ).run((context) -> {
                    assertNotFound(context, COMMON);
                    assertNotFound(context, UNCOMMON);
                });
    }

    @Test
    @ResourceLock("COMMON")
    void testConfigWithDevtoolsAndProfile() {
        new ApplicationContextRunner()
                .withSystemProperties(
                    ProfileUtils.activeProfilesString(STUBDEV),
                    STUB_DEV_PROPS_PREFIX + ".defaultUuid=123-456-789"
                )
                .withConfiguration(
                        AutoConfigurations.of(LocalDevToolsAutoConfiguration.class, IfsStubDevAutoConfiguration.class)
                ).run((context) -> {
                    assertFound(context, COMMON);
                    assertNotFound(context, UNCOMMON);
                });
    }

    @Test
    @ResourceLock("COMMON")
    void testConfigWithDevtoolsNoProfile() {
        new ApplicationContextRunner()
                .withConfiguration(
                        AutoConfigurations.of(LocalDevToolsAutoConfiguration.class, IfsStubDevAutoConfiguration.class)
                ).run((context) -> {
                    assertNotFound(context, COMMON);
                    assertNotFound(context, UNCOMMON);
                });
    }

    @Test
    @ResourceLock("COMMON")
    void testConfigNoDevtoolsNoProfile() {
        new ApplicationContextRunner()
                .withConfiguration(
                        AutoConfigurations.of(IfsStubDevAutoConfiguration.class)
                ).run((context) -> {
                    assertNotFound(context, COMMON);
                    assertNotFound(context, UNCOMMON);
                });
    }

    private static void assertNotFound(ApplicationContext context, List<Class> clzs) {
        clzs.stream().forEach(clz -> assertThrows(BeansException.class, () -> context.getBean(clz)));
    }

    private static void assertFound(ApplicationContext context, List<Class> clzs) {
        clzs.stream().forEach(clz -> assertThat(context.getBean(clz), is(notNullValue())));
    }
}