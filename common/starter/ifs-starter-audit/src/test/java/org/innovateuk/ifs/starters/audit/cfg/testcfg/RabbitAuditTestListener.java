package org.innovateuk.ifs.starters.audit.cfg.testcfg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.api.audit.AuditMessage;
import org.innovateuk.ifs.starters.audit.cfg.AuditConfigurationProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.starters.audit.cfg.rabbit.RabbitAuditConfiguration.AUDIT_OBJECT_MAPPER_BEAN_NAME;

public class RabbitAuditTestListener {

    @Autowired
    @Qualifier(AUDIT_OBJECT_MAPPER_BEAN_NAME)
    private ObjectMapper objectMapper;

    @Autowired
    private AuditConfigurationProperties auditConfigurationProperties;

    private List<AuditMessage> auditMessages = new ArrayList<>();

    @RabbitListener(queues = {"audit"})
    public void receive(@Payload String auditMessage) throws JsonProcessingException {
        auditMessages.add(objectMapper.readValue(auditMessage, AuditMessage.class));
    }


}
