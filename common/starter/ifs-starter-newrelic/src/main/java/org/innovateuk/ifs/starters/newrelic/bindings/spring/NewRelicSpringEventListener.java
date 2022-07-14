package org.innovateuk.ifs.starters.newrelic.bindings.spring;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.starters.newrelic.NewRelicEventChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.*;

import java.util.HashMap;
import java.util.Map;

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

    private void handleContextEvent(ApplicationContextEvent applicationContextEvent) {
        log.trace(RECEIVED_SPRING_EVENT + applicationContextEvent.toString());
        Map<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("Spring Application Name", applicationContextEvent.getApplicationContext().getApplicationName());
        eventAttributes.put("Spring Display Name", applicationContextEvent.getApplicationContext().getDisplayName());
        eventAttributes.put("Start Date", applicationContextEvent.getApplicationContext().getStartupDate());
        eventAttributes.put("Event Time", applicationContextEvent.getTimestamp());
        newRelicEventChannel.sendEvent(applicationContextEvent.getClass().getSimpleName(), eventAttributes);
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent cre) {
        handleContextEvent(cre);
    }

    @EventListener
    public void handleContextStart(ContextStartedEvent cse) {
        handleContextEvent(cse);
    }

    @EventListener
    public void handleContextClose(ContextClosedEvent cce) {
        handleContextEvent(cce);
    }

    @EventListener
    public void handleContextStop(ContextStoppedEvent cse) {
        handleContextEvent(cse);
    }

}
