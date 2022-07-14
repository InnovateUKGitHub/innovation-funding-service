package org.innovateuk.ifs.starters.newrelic;

import com.newrelic.api.agent.NewRelic;
import org.springframework.context.event.ApplicationContextEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapts different types of events to conform the new relic event api and pushed them via the new relic client
 *
 * New relic will need to be running and configured to push these to the new relic server. If nothing is configured it
 * will perform a no-op operations.
 *
 * For IFS this means running the app within a k8s container with a licence key specified in the configuration.
 *
 * By default, all events will have the following auto-populated -:
 *          Timestamp
 *          App Id
 *          App Name
 *          Entity Guid
 *          Host
 *          Real Agent Id
 *          Tags.Account
 *          Tags.Account Id
 *          Tags.Env
 *          Tags.Trusted Account Id
 *
 * Further tags can be specified via the Map<String, Object> eventAttributes
 */
public class NewRelicEventChannel {

    private static final String APP_ERROR_EVENT = "APP_ERROR_EVENT";

    /**
     * Ad-hoc call to the event api
     * @param eventType the event type (this will be searchable as FROM eventType in new relic.
     * @param eventAttributes any additional attributes
     */
    public void sendCustomEvent(String eventType, Map<String, Object> eventAttributes) {
        NewRelic.getAgent().getInsights().recordCustomEvent(eventType, eventAttributes);
    }

    /**
     * Error are tracked anyway in new relic however it is often useful to be able to monitor specific error
     * conditions for manual follow up. In addition it is well alertable, reported and searchable in new relic
     * rather than trawling logs.
     * @param ex the exception to track.
     * @param clz originating class
     */
    public void sendErrorEvent(Exception ex, Class<?> clz) {
        Map<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("Class", clz.getCanonicalName());
        eventAttributes.put("Exception", ex.getClass().getCanonicalName());
        NewRelic.getAgent().getInsights().recordCustomEvent(APP_ERROR_EVENT, eventAttributes);
    }

    /**
     * Adapt from ApplicationContextEvents to new relic api event calls.
     * @param applicationContextEvent subclass of applicationContextEvent
     */
    public void sendApplicationContextEvent(ApplicationContextEvent applicationContextEvent) {
        Map<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("Spring Application Name", applicationContextEvent.getApplicationContext().getApplicationName());
        eventAttributes.put("Spring Display Name", applicationContextEvent.getApplicationContext().getDisplayName());
        eventAttributes.put("Start Date", applicationContextEvent.getApplicationContext().getStartupDate());
        eventAttributes.put("Event Time", applicationContextEvent.getTimestamp());
        NewRelic.getAgent().getInsights().recordCustomEvent(applicationContextEvent.getClass().getSimpleName(), eventAttributes);
    }
}
