package org.innovateuk.ifs.starters.audit.cfg.rabbit;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.starters.audit.AuditChannel;
import org.innovateuk.ifs.starters.audit.cfg.AuditAutoConfiguration;
import org.innovateuk.ifs.starters.audit.cfg.AuditConfigurationProperties;
import org.innovateuk.ifs.starters.audit.rabbit.RabbitAuditChannel;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@EnableRabbit
@Profile(IfsProfileConstants.AMQP_PROFILE)
@EnableConfigurationProperties(AuditConfigurationProperties.class)
public class RabbitAuditConfiguration {

    public static final String AUDIT_OBJECT_MAPPER_BEAN_NAME = "auditObjectMapper";
    public static final String AUDIT_QUEUE_BEAN_NAME = "auditQueue";

    @Autowired
    private AuditConfigurationProperties auditConfigurationProperties;

    @Bean
    public FanoutExchange exchange() {
        return new FanoutExchange(auditConfigurationProperties.getAuditExchangeName());
    }

    @Bean
    public Binding binding(Queue queue, FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }

    @Bean
    public AuditChannel rabbitAuditChannel() {
        return new RabbitAuditChannel();
    }

    @Bean(name = {AUDIT_QUEUE_BEAN_NAME})
    public Queue auditQueue() {
        return new Queue(auditConfigurationProperties.getAuditQueueName(), true);
    }

    @Bean(name = {AUDIT_OBJECT_MAPPER_BEAN_NAME})
    public ObjectMapper auditObjectMapper() {
        return new ObjectMapper();
    }
}
