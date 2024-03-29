package org.innovateuk.ifs.starters.stubdev.cfg;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.innovateuk.ifs.IfsProfileConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static org.innovateuk.ifs.starters.stubdev.cfg.StubDevConfigurationProperties.STUB_DEV_PROPS_PREFIX;

@Getter
@Setter
@ToString
@Configuration
@ConditionalOnProperty(IfsProfileConstants.STUBDEV)
@ConfigurationProperties(prefix = STUB_DEV_PROPS_PREFIX)
public class StubDevConfigurationProperties {

    public static final String STUB_DEV_PROPS_PREFIX = "ifs.starters.stubdev";

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

    /**
     * Use this to serve static files from the filesystem
     */
    private String webCoreTemplates = "/ifs-web-service/ifs-web-core/src/main/resources/templates/";

    /**
     * Use this to serve static files from the filesystem
     */
    private String assessmentCommonsTemplates = "/common/ifs-assessment-commons/src/main/resources/templates/";

    /**
     * Enable trace logging in thymeleaf to log template calls
     */
    private boolean logThymeLeafTemplates;

    /**
     * This is the default stub user
     */
    private String defaultUuid;

    /**
     * Rewrite rules for rewriting urls in responses
     * See Rewrite Filter
     */
    private List<RewriteRule> rewriteRules;
}
