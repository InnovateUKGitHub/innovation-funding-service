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
 *
 * Auto Configuration for Stub Dev mode. Consists of a number of components, see each for details.
 *
 *
 *
 * Guarantee that we only run in dev by running conditionally on devtools class LiveReloadServer.
 * @ConditionalOnClass(name = "org.springframework.boot.devtools.livereload.LiveReloadServer")
 * @AutoConfigureAfter(name = "org.springframework.boot.devtools.autoconfigure.LocalDevToolsAutoConfiguration")
 *
 */
@Slf4j
@Configuration
@Profile(IfsProfileConstants.STUBDEV)
@ConditionalOnClass(name = "org.springframework.boot.devtools.livereload.LiveReloadServer")
@AutoConfigureAfter(name = "org.springframework.boot.devtools.autoconfigure.LocalDevToolsAutoConfiguration")
public class IfsStubDevAutoConfiguration {

    @Autowired
    private StubDevConfigurationProperties stubDevConfigurationProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        log.info("XXXXXXXXXXXXXXXXXXXXXXXXX STUB DEV XXXXXXXXXXXXXXXXXXXXXXXXX");
        log.info(stubDevConfigurationProperties.toString());
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
        resolver.setApplicationContext(this.applicationContext);
        resolver.setPrefix(stubDevConfigurationProperties.getProjectRootDirectory()
                + "/ifs-web-service/ifs-web-core/src/main/resources/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resolver.setCacheable(false);
        resolver.setCheckExistence(true);
        return resolver;
    }

}
