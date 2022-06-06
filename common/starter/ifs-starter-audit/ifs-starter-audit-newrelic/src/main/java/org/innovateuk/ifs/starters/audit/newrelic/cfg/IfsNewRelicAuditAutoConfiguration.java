package org.innovateuk.ifs.starters.audit.newrelic.cfg;

import org.innovateuk.ifs.api.audit.AuditChannel;
import org.innovateuk.ifs.starters.audit.log.cfg.AuditAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfigureBefore(AuditAutoConfiguration.class)
@EnableConfigurationProperties(NewRelicConfigurationProperties.class)
public class IfsNewRelicAuditAutoConfiguration {

    @Bean
    public AuditChannel newRelicAuditChannel() {
        return new NewRelicAuditChannel();
    }


}
