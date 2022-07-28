package org.innovateuk.ifs.starters.audit.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.api.audit.Audit;
import org.innovateuk.ifs.api.audit.AuditMessageBuilder;
import org.innovateuk.ifs.api.audit.AuditType;
import org.innovateuk.ifs.starters.audit.cfg.AuditConfigurationProperties;
import org.innovateuk.ifs.starters.audit.cfg.testcfg.RabbitAuditTestConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.innovateuk.ifs.starters.audit.cfg.testcfg.RabbitAuditTestConfiguration.CONTEXT_RESOURCE_LOCK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {RabbitAuditTestConfiguration.class})
@EnableConfigurationProperties(AuditConfigurationProperties.class)
class RabbitAuditMockTest {

    @MockBean
    private ObjectMapper objectMapper;

    @Autowired
    private Audit audit;

    @Test
    @ResourceLock(CONTEXT_RESOURCE_LOCK)
    void audit() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> {
            audit.audit(AuditMessageBuilder.builder(AuditType.MISC).payload("{json: 'ddd'}").build());
        });
        Assertions.assertEquals(JsonProcessingException.class, thrown.getCause().getClass());
    }

}
