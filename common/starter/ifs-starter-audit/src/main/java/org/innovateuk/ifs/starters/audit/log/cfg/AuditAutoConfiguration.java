package org.innovateuk.ifs.starters.audit.log.cfg;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.api.audit.AuditChannel;
import org.innovateuk.ifs.starters.audit.AuditAdapter;
import org.innovateuk.ifs.starters.audit.log.LogAuditChannel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AuditAutoConfiguration {

    @Bean
    public AuditAdapter auditAdapter() {
        return new AuditAdapter();
    }

    /**
     * Default is to fall back to a log provider if andy of the other audit channels are unavailable.
     */
    @Bean
    @ConditionalOnMissingBean(AuditChannel.class)
    public AuditChannel logAuditChannel() {
        return new LogAuditChannel();
    }

}
