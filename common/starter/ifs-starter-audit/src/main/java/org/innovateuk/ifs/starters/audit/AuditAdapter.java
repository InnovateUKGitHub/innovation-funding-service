package org.innovateuk.ifs.starters.audit;

import org.innovateuk.ifs.api.audit.Audit;
import org.innovateuk.ifs.api.audit.AuditMessage;
import org.innovateuk.ifs.api.audit.AuditChannel;
import org.springframework.beans.factory.annotation.Autowired;

public class AuditAdapter implements Audit {

    @Autowired
    public AuditChannel auditChannel;

    @Override
    public void audit(AuditMessage auditMessage) {
        auditChannel.doSendMessage(auditMessage);
    }
}
