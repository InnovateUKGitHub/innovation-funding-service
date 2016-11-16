package com.worth.ifs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;


@SpringBootApplication
@EnableScheduling
public class Application extends SpringBootServletInitializer {
    private static final Log log = LogFactory.getLog(Application.class);


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        log.info("Spring Application builder configure method");
        log.info("======== com.worth.ifs.Application.configure()");
        return application.sources(Application.class);
    }
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        log.info("======== com.worth.ifs.Application.onStartup()");
    }

    /**
     * DefaultFormattingConversionService registered to allow the injection of properties lists into @Value fields
     */
    @Bean
    public static ConversionService conversionService() {
        return new DefaultFormattingConversionService();
    }

    public static void main(String[] args) throws Exception {
        log.info("======== com.worth.ifs.Application.main()");
        SpringApplication.run(Application.class, args);
    }
}