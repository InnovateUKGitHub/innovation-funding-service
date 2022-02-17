package org.innovateuk.ifs.starters.stubdev;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.starters.stubdev.filter.RewriteFilter;
import org.innovateuk.ifs.starters.stubdev.security.StubAuthFilter;
import org.innovateuk.ifs.starters.stubdev.util.TimerAspect;
import org.innovateuk.ifs.starters.stubdev.util.WarningLogger;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.servlet.Filter;


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

}
