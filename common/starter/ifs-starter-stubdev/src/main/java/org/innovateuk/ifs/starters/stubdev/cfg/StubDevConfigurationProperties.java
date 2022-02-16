package org.innovateuk.ifs.starters.stubdev.cfg;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static org.innovateuk.ifs.starters.stubdev.Constants.STUB_DEV_PROPS_PREFIX;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = STUB_DEV_PROPS_PREFIX)
public class StubDevConfigurationProperties {

    /**
     * Log method calls through the stack via AOP
     */
    private boolean enableMethodLogging = false;


    /**
     * Time and log method calls through the stack via AOP
     */
    private boolean enableMethodTiming = false;


}
