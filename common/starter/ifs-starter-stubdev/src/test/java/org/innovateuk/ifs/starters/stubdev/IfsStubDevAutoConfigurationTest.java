package org.innovateuk.ifs.starters.stubdev;

import org.innovateuk.ifs.starters.stubdev.util.WarningLogger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.devtools.autoconfigure.LocalDevToolsAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IfsStubDevAutoConfigurationTest {

    @Test
    public void auditContextConfigurationNonK8s() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(LocalDevToolsAutoConfiguration.class, IfsStubDevAutoConfiguration.class)
            );
        contextRunner.run((context) -> {
            assertThat(context.getBean(WarningLogger.class), is(notNullValue()));
        });
    }

    @Test
    public void checkContextInNonDevMode() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(
                    AutoConfigurations.of(IfsStubDevAutoConfiguration.class)
            );
        contextRunner.run((context) -> {
            assertThrows(BeansException.class, () -> context.getBean(WarningLogger.class));
        });
    }

}