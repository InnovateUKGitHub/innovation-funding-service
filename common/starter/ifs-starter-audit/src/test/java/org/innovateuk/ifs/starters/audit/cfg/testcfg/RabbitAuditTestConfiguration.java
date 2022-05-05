package org.innovateuk.ifs.starters.audit.cfg.testcfg;

import com.github.fridujo.rabbitmq.mock.compatibility.MockConnectionFactoryFactory;
import org.innovateuk.ifs.starters.audit.cfg.AuditAutoConfiguration;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(AuditAutoConfiguration.class)
public class RabbitAuditTestConfiguration {

    @Autowired
    private Queue queue;

    public static final String CONTEXT_RESOURCE_LOCK = "CONTEXT_RESOURCE_LOCK";

    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(MockConnectionFactoryFactory.build().enableConsistentHashPlugin());
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public RabbitAuditTestListener rabbitAuditTestListener() {
        return new RabbitAuditTestListener();
    }

    @Bean
    public RabbitListenerContainerFactory rabbitListenerContainerFactory() {
        DirectRabbitListenerContainerFactory container = new DirectRabbitListenerContainerFactory();
        container.setConnectionFactory(connectionFactory());
        return container;
    }
}
