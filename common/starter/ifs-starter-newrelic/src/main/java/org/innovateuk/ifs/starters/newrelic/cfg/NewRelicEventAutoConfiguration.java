package org.innovateuk.ifs.starters.newrelic.cfg;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.starters.newrelic.NewRelicEventChannel;
import org.innovateuk.ifs.starters.newrelic.bindings.spring.NewRelicSpringEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class NewRelicEventAutoConfiguration {

    @Bean
    public NewRelicEventChannel newRelicEventChannel() {
        return new NewRelicEventChannel();
    }

    @Bean
    public NewRelicSpringEventListener newRelicSpringEventListener() {
        return new NewRelicSpringEventListener();
    }

}
