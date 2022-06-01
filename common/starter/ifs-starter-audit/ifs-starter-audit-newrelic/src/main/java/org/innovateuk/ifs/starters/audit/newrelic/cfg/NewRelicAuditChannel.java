package org.innovateuk.ifs.starters.audit.newrelic.cfg;

import com.newrelic.api.agent.NewRelic;
import org.innovateuk.ifs.api.audit.AuditChannel;
import org.innovateuk.ifs.api.audit.AuditMessage;

import java.util.HashMap;
import java.util.Map;

public class NewRelicAuditChannel implements AuditChannel {

    @Override
    public void doSendMessage(AuditMessage auditMessage) {
        Map<String, Object> eventAttributes = new HashMap<String, Object>();
        NewRelic.getAgent().getInsights().recordCustomEvent("MyCustomEvent", eventAttributes);
    }
}
