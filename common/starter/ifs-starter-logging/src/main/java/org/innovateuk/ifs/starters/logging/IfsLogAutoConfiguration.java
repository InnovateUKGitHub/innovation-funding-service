package org.innovateuk.ifs.starters.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
public class IfsLogAutoConfiguration {

    @PostConstruct
    public void log() {
        log.info("Logback configured with ifs-starter-logging/src/main/resources/logback-ifs.xml");
    }

    // Expecting some configuration for sleuth tracing in AMQP to follow

}
