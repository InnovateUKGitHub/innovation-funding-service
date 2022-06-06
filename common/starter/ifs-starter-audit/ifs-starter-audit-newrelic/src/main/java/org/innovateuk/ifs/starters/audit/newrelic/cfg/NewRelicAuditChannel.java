package org.innovateuk.ifs.starters.audit.newrelic.cfg;

import com.newrelic.api.agent.NewRelic;
import org.innovateuk.ifs.api.audit.AuditChannel;
import org.innovateuk.ifs.api.audit.AuditMessage;

import java.util.HashMap;
import java.util.Map;

public class NewRelicAuditChannel implements AuditChannel {

    @Override
    public void doSendMessage(AuditMessage auditMessage) {
        Map<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("userId", auditMessage.getUserId());
        eventAttributes.put("created", auditMessage.getCreated());
        eventAttributes.put("span", auditMessage.getSpanId());
        eventAttributes.put("trace", auditMessage.getTraceId());
        eventAttributes.put("uuid", auditMessage.getUuid());
        eventAttributes.put("payload", auditMessage.getPayload());
        NewRelic.getAgent().getInsights().recordCustomEvent(auditMessage.getAuditType(), eventAttributes);
    }
}
