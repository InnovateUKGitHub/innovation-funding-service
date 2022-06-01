package org.innovateuk.ifs.starters.audit.log;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.api.audit.AuditMessage;
import org.innovateuk.ifs.api.audit.AuditChannel;

@Slf4j
public class LogAuditChannel implements AuditChannel {
    @Override
    public void doSendMessage(AuditMessage auditMessage) {
        log.info(auditMessage.toString());
    }
}
