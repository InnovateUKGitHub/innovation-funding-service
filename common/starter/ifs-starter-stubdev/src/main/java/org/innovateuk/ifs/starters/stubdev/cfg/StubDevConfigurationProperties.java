package org.innovateuk.ifs.starters.stubdev.cfg;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static org.innovateuk.ifs.starters.stubdev.Constants.STUB_DEV_PROPS_PREFIX;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = STUB_DEV_PROPS_PREFIX)
public class StubDevConfigurationProperties {

    /**
     * Time and log method calls through the stack via AOP
     */
    private boolean enableClientMethodTiming = false;

    /**
     * Enables the HMTL post processor Thymeleaf3ValidHtmlEnforcerPostProcessorHandler
     */
    private boolean validateHtml = false;

    /**
     * Use this to serve static files from the filesystem
     */
    private String projectRootDirectory = "unset";


}
