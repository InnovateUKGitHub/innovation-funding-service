package org.innovateuk.ifs.starters.stubdev;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.starters.stubdev.cfg.StubDevConfigurationProperties;
import org.innovateuk.ifs.starters.stubdev.filter.RewriteFilter;
import org.innovateuk.ifs.starters.stubdev.security.StubAuthFilter;
import org.innovateuk.ifs.starters.stubdev.thymeleaf.IfsThymeleafPostProcessorDialect;
import org.innovateuk.ifs.starters.stubdev.util.WarningLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import java.nio.charset.StandardCharsets;

import static org.innovateuk.ifs.starters.stubdev.Constants.STUB_DEV_PROPS_PREFIX;


/**
 * Auto Configuration for Stub Dev mode. Consists of a number of components, see each for details.
 *
 * Guarantee that we only run in dev by running conditionally on devtools class LiveReloadServer.
 * @ConditionalOnClass(name = "org.springframework.boot.devtools.livereload.LiveReloadServer")
 * @AutoConfigureAfter(name = "org.springframework.boot.devtools.autoconfigure.LocalDevToolsAutoConfiguration")
 *
 */
@Slf4j
@Configuration
@Profile(IfsProfileConstants.STUBDEV)
@EnableConfigurationProperties(StubDevConfigurationProperties.class)
@ConditionalOnClass(name = "org.springframework.boot.devtools.livereload.LiveReloadServer")
@AutoConfigureAfter(name = "org.springframework.boot.devtools.autoconfigure.LocalDevToolsAutoConfiguration")
public class IfsStubDevAutoConfiguration {

    @Autowired
    private StubDevConfigurationProperties stubDevConfigurationProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void logSettings() {
        log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        log.info("XXXXXXXXXXXXXXXXXXXXXXXXX STUB DEV XXXXXXXXXXXXXXXXXXXXXXXXX");
        log.info(stubDevConfigurationProperties.getProjectRootDirectory());
        log.info(stubDevConfigurationProperties.getWebCoreTemplates());
        log.info("" + stubDevConfigurationProperties.isEnableClientMethodTiming());
        log.info("" + stubDevConfigurationProperties.isValidateHtml());
        log.info("" + stubDevConfigurationProperties.isLogThymeLeafTemplates());
        log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    }

    @Bean
    public WarningLogger getScheduled() {
        return new WarningLogger();
    }

    @Bean
    public Filter filter() {
        return new RewriteFilter();
    }

    @Bean
    public StubAuthFilter stubAuthFilter() {
        return new StubAuthFilter();
    }

    @Bean
    @ConditionalOnProperty(prefix=STUB_DEV_PROPS_PREFIX, name="validateHtml", havingValue="true")
    public IDialect ifsThymeleafPostProcessorDialect() {
        return new IfsThymeleafPostProcessorDialect();
    }

    @Bean
    public ITemplateResolver webCoreTemplateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setName("WEB_CORE_TEMPLATE_RESOLVER");
        resolver.setApplicationContext(this.applicationContext);
        resolver.setPrefix(stubDevConfigurationProperties.getProjectRootDirectory()
                + stubDevConfigurationProperties.getWebCoreTemplates());
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resolver.setCacheable(false);
        resolver.setCheckExistence(true);
        return resolver;
    }

}
