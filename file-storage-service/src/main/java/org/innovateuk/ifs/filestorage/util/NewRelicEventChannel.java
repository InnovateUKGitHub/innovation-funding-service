package org.innovateuk.ifs.filestorage.util;

import com.newrelic.api.agent.NewRelic;

import java.util.HashMap;
import java.util.Map;

public class NewRelicEventChannel {

    public void doSendMessage() {
        Map<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("test", "test");
        NewRelic.getAgent().getInsights().recordCustomEvent("TEST_EVENT", eventAttributes);
    }
}
