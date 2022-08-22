package org.innovateuk.ifs.starters.audit.cfg.log;

import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.starters.audit.AuditChannel;
import org.innovateuk.ifs.starters.audit.log.LogAuditChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile(IfsProfileConstants.DISABLE_AMQP)
public class LogAuditConfiguration {

    @Bean
    public AuditChannel logAuditChannel() {
        return new LogAuditChannel();
    }

}
