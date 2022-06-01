package org.innovateuk.ifs.starters.audit.newrelic.cfg;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = NewRelicConfigurationProperties.NR_CONFIG_PREFIX)
public class NewRelicConfigurationProperties {

    public static final String NR_CONFIG_PREFIX = "ifs.starter.audit.newrelic";
}
