package org.innovateuk.ifs.starters.audit.cfg;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.starters.audit.AuditAdapter;
import org.innovateuk.ifs.starters.audit.cfg.log.LogAuditConfiguration;
import org.innovateuk.ifs.starters.audit.cfg.rabbit.RabbitAuditConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@Configuration
@Import({RabbitAuditConfiguration.class, LogAuditConfiguration.class})
@AutoConfigureAfter(RabbitAutoConfiguration.class)
public class AuditAutoConfiguration {

    public static final String AMQP_PROFILE = "AMQP";
    public static final String NOT_AMQP_PROFILE = "!" + AMQP_PROFILE;

    @Bean
    public AuditAdapter auditAdapter() {
        return new AuditAdapter();
    }


}
