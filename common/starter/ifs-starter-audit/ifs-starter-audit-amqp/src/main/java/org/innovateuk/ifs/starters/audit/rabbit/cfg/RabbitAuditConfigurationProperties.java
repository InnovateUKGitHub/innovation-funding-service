package org.innovateuk.ifs.starters.audit.rabbit.cfg;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ConfigurationProperties(prefix = RabbitAuditConfigurationProperties.AUDIT_CONFIG_PREFIX)
public class RabbitAuditConfigurationProperties {

    public static final String AUDIT_CONFIG_PREFIX = "ifs.starter.audit.rabbit";

    @NotBlank
    private String auditQueueName;

    @NotBlank
    private String auditExchangeName;
}
