package org.innovateuk.ifs.starters.audit.cfg;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

import static org.innovateuk.ifs.starters.audit.cfg.AuditConfigurationProperties.AUDIT_CONFIG_PREFIX;

@Getter
@Setter
@ConfigurationProperties(prefix = AUDIT_CONFIG_PREFIX)
public class AuditConfigurationProperties {

    public static final String AUDIT_CONFIG_PREFIX = "ifs.starter.audit";

    @NotBlank
    private String auditQueueName;

    @NotBlank
    private String auditExchangeName;
}
