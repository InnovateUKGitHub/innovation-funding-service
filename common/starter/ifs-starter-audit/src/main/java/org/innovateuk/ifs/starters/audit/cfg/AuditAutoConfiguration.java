package org.innovateuk.ifs.starters.audit.cfg;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.starters.audit.AuditAdapter;
import org.innovateuk.ifs.starters.audit.cfg.log.LogAuditConfiguration;
import org.innovateuk.ifs.starters.audit.cfg.rabbit.RabbitAuditConfiguration;
import org.innovateuk.ifs.starters.messaging.cfg.MessagingAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@Configuration
@Import({RabbitAuditConfiguration.class, LogAuditConfiguration.class})
@AutoConfigureAfter({RabbitAutoConfiguration.class, MessagingAutoConfiguration.class})
public class AuditAutoConfiguration {

    @Bean
    public AuditAdapter auditAdapter() {
        return new AuditAdapter();
    }

}
