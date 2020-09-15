package org.innovateuk.ifs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableCircuitBreaker
@PropertySource(value = { "classpath:/application.properties", "classpath:/applicationservice.properties" })
@SpringBootApplication(exclude=org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class)
@EnableAspectJAutoProxy(proxyTargetClass=true)
@EnableAsync
public class Application extends SpringBootServletInitializer {

    private static final Log LOG = LogFactory.getLog(Application.class);

    @Bean
    public static ConversionService conversionService() {
        return new DefaultFormattingConversionService();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        LOG.info("IFS Application builder configure method");
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        LOG.info("IFS boot Application main method");
        SpringApplication.run(Application.class, args);
    }
}
