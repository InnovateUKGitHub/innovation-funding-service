package org.innovateuk.ifs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@PropertySource(value = {"classpath:application.yml", "classpath:/application-web-core.yml"})
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass=true)
@EnableAsync
public class Application extends SpringBootServletInitializer {

    @Bean
    public static ConversionService conversionService() {
        return new DefaultFormattingConversionService();
    }

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
