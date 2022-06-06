package org.innovateuk.ifs.starters.audit.newrelic.cfg;

import org.innovateuk.ifs.api.audit.AuditMessageBuilder;
import org.innovateuk.ifs.api.audit.AuditType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = NewRelicAuditChannel.class)
class NewRelicAuditChannelTest {

    @Autowired
    private NewRelicAuditChannel newRelicAuditChannel;

    @Test
    void doSendMessage() {
        newRelicAuditChannel.doSendMessage(
            AuditMessageBuilder
                .builder(AuditType.FILE_STORAGE_SUCCESS_EVENT)
                .userId("test")
                .build()
        );
    }
}