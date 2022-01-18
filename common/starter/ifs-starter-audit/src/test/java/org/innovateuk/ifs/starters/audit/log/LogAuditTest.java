package org.innovateuk.ifs.starters.audit.log;

import org.innovateuk.ifs.api.audit.Audit;
import org.innovateuk.ifs.api.audit.AuditMessageBuilder;
import org.innovateuk.ifs.api.audit.AuditType;
import org.innovateuk.ifs.starters.audit.AuditAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {LogAuditChannel.class, AuditAdapter.class})
public class LogAuditTest {

    @Autowired
    private Audit audit;

    @Test
    public void audit() {
        audit.audit(AuditMessageBuilder.builder(AuditType.MISC).payload("{json: 'ddd'}").build());
    }

}
