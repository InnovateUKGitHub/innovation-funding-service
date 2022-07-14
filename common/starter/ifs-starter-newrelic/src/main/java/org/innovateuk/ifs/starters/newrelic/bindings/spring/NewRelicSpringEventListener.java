package org.innovateuk.ifs.starters.newrelic.bindings.spring;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.starters.newrelic.NewRelicEventChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.*;

/**
 * Receive and automatically propagate spring events to the NewRelicEventChannel
 *
 * Events will be visible and searchable in new relic.
 *
 * Currently, just listens for context events but there is no limit to this.
 *
 * Implementing services can easily define their own listeners including adding custom events.
 *
 * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/ApplicationEvent.html
 */
@Slf4j
public class NewRelicSpringEventListener {

    private  static final String RECEIVED_SPRING_EVENT = "Received spring event - ";

    @Autowired
    private NewRelicEventChannel newRelicEventChannel;

    @EventListener
    public void handleContextStart(ContextRefreshedEvent cse) {
        log.trace(RECEIVED_SPRING_EVENT + cse.toString());
        newRelicEventChannel.sendApplicationContextEvent(cse);
    }

    @EventListener
    public void handleContextStart(ContextStartedEvent cse) {
        log.trace(RECEIVED_SPRING_EVENT + cse.toString());
        newRelicEventChannel.sendApplicationContextEvent(cse);
    }

    @EventListener
    public void handleContextStart(ContextClosedEvent cse) {
        log.trace(RECEIVED_SPRING_EVENT + cse.toString());
        newRelicEventChannel.sendApplicationContextEvent(cse);
    }

    @EventListener
    public void handleContextStart(ContextStoppedEvent cse) {
        log.trace(RECEIVED_SPRING_EVENT + cse.toString());
        newRelicEventChannel.sendApplicationContextEvent(cse);
    }

}
