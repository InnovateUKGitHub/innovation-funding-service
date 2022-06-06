package org.innovateuk.ifs.starters.audit.rabbit;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.api.audit.AuditMessage;
import org.innovateuk.ifs.api.audit.AuditChannel;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.innovateuk.ifs.starters.audit.rabbit.cfg.RabbitAuditAutoConfiguration.AUDIT_OBJECT_MAPPER_BEAN_NAME;
import static org.innovateuk.ifs.starters.audit.rabbit.cfg.RabbitAuditAutoConfiguration.AUDIT_QUEUE_BEAN_NAME;

@Slf4j
public class RabbitAuditChannel implements AuditChannel {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    @Qualifier(AUDIT_OBJECT_MAPPER_BEAN_NAME)
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier(AUDIT_QUEUE_BEAN_NAME)
    private Queue queue;

    @Override
    public void doSendMessage(AuditMessage auditMessage) {
        try {
            rabbitTemplate.convertAndSend(queue.getName(), objectMapper.writeValueAsString(auditMessage));
        } catch (JsonProcessingException e) {
            log.error("Unable to marshall audit message as json", e);
            throw new RuntimeException("Unable to marshall audit message as json", e);
        }
    }
}
