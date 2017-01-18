package org.innovateuk.ifs.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * A base class for any web modules that plan to use Thymeleaf 3.  The configuration here excludes the Spring Boot
 * auto-configuration, which is Thymeleaf 2-based
 */
@SpringBootApplication(exclude=org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class)
@EnableAspectJAutoProxy(proxyTargetClass=true)
public abstract class Thymeleaf3Application extends SpringBootServletInitializer {
}