package org.innovateuk.ifs.starters.audit;


import org.innovateuk.ifs.api.audit.AuditMessage;

public interface AuditChannel {
    void doSendMessage(AuditMessage auditMessage);
}
