package org.innovateuk.ifs.api.audit;


public interface AuditChannel {
    void doSendMessage(AuditMessage auditMessage);
}
