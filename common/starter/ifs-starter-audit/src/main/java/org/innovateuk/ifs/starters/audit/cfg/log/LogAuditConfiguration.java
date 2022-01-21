package org.innovateuk.ifs.starters.audit.cfg.log;

import org.innovateuk.ifs.starters.audit.AuditChannel;
import org.innovateuk.ifs.starters.audit.cfg.AuditAutoConfiguration;
import org.innovateuk.ifs.starters.audit.log.LogAuditChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile(AuditAutoConfiguration.NOT_AMQP_PROFILE)
public class LogAuditConfiguration {

    @Bean
    public AuditChannel logAuditChannel() {
        return new LogAuditChannel();
    }

}