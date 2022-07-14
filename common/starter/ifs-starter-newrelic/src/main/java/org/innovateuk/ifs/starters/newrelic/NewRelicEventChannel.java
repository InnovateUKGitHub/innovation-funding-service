package org.innovateuk.ifs.starters.newrelic;

import com.newrelic.api.agent.NewRelic;

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

    /**
     * Ad-hoc call to the event api
     * @param eventType the event type (this will be searchable as FROM eventType in new relic.
     * @param eventAttributes any additional attributes
     */
    public void sendEvent(String eventType, Map<String, Object> eventAttributes) {
        NewRelic.getAgent().getInsights().recordCustomEvent(eventType, eventAttributes);
    }

    /**
     * Error are tracked anyway in new relic however it is often useful to be able to monitor specific error
     * conditions for manual follow up. In addition, it is alertable, reported and searchable in new relic
     * rather than trawling logs.
     * @param ex the exception to track.
     * @param clz originating class
     */
    public void sendErrorEvent(String eventType, Exception ex, Class<?> clz, Map<String, Object> eventAttributes) {
        Map<String, Object> merged = new HashMap<>();
        merged.putAll(eventAttributes);
        merged.put("Class", clz.getCanonicalName());
        merged.put("Exception", ex.getClass().getCanonicalName());
        NewRelic.getAgent().getInsights().recordCustomEvent(eventType, merged);
    }
}
