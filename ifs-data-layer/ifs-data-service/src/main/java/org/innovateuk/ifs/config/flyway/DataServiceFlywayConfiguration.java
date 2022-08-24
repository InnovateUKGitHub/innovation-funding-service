package org.innovateuk.ifs.config.flyway;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "ifs.flyway")
public class DataServiceFlywayConfiguration {

    private boolean repair = false;

}
