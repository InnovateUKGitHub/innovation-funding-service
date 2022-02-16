package org.innovateuk.ifs.starters.stubdev;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;


/**
 * Guarantee that we only run in dev mode by running conditionally on devtools class LiveReloadServer
 */
@Slf4j
@Configuration
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
}
