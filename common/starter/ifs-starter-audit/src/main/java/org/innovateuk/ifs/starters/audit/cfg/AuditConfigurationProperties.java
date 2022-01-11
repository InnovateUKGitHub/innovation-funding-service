package org.innovateuk.ifs.starters.audit.cfg;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ConfigurationProperties(prefix = "ifs.starter.audit")
public class AuditConfigurationProperties {

    @NotBlank
    private String auditQueueName;

    @NotBlank
    private String auditExchangeName;
}
