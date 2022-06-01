package org.innovateuk.ifs.starters.audit.rabbit;

import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.api.audit.Audit;
import org.innovateuk.ifs.api.audit.AuditMessageBuilder;
import org.innovateuk.ifs.api.audit.AuditType;
import org.innovateuk.ifs.starters.audit.rabbit.cfg.IfsRabbitContextInitializer;
import org.innovateuk.ifs.starters.audit.rabbit.cfg.RabbitAuditConfigurationProperties;
import org.innovateuk.ifs.starters.audit.rabbit.cfg.testcfg.RabbitAuditTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles({IfsProfileConstants.AMQP_PROFILE})
@ContextConfiguration(initializers = {IfsRabbitContextInitializer.class})
@SpringBootTest(classes = {RabbitAuditTestConfiguration.class})
@EnableConfigurationProperties(RabbitAuditConfigurationProperties.class)
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
