package org.innovateuk.ifs.starters.audit.rabbit;

import org.innovateuk.ifs.api.audit.Audit;
import org.innovateuk.ifs.api.audit.AuditMessageBuilder;
import org.innovateuk.ifs.api.audit.AuditType;
import org.innovateuk.ifs.starters.audit.cfg.AuditConfigurationProperties;
import org.innovateuk.ifs.starters.audit.cfg.testcfg.RabbitAuditTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"AMQP"})
@SpringBootTest(classes = {RabbitAuditTestConfiguration.class})
@EnableConfigurationProperties(AuditConfigurationProperties.class)
public class RabbitAuditTest {

    @Autowired
    private Audit audit;

    @Test
    public void audit() {
        for(int i=0;i<100;i++) {
            audit.audit(AuditMessageBuilder.builder(AuditType.MISC).payload("{json: 'ddd'}").build());
        }
    }

}
