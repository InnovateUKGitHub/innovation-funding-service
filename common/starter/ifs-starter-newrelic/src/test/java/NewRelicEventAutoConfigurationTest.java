import org.innovateuk.ifs.starters.newrelic.NewRelicEventChannel;
import org.innovateuk.ifs.starters.newrelic.bindings.spring.NewRelicSpringEventListener;
import org.innovateuk.ifs.starters.newrelic.cfg.NewRelicEventAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class NewRelicEventAutoConfigurationTest {

    @Test
    @ResourceLock("ApplicationContextRunner")
    void testConfigWithSimpleCacheProfile() {
        new ApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(NewRelicEventAutoConfiguration.class)
            ).run((context) -> {
                assertThat(context.getBean(NewRelicEventChannel.class), is(notNullValue()));
                assertThat(context.getBean(NewRelicSpringEventListener.class), is(notNullValue()));
            });
    }
}
