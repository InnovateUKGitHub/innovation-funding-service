package org.innovateuk.ifs;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.cfg.IfsApplicationServiceApplicationConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@PropertySource(value = {"classpath:application.properties", "classpath:/application-web-core.properties"})
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass=true)
@EnableAsync
@Import(IfsApplicationServiceApplicationConfiguration.class)
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        log.info("IFS Application builder configure method");
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        log.info("IFS boot Application main method");
        SpringApplication.run(Application.class, args);
    }
}
