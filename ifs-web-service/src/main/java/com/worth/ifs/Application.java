package com.worth.ifs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;


@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    private static final Log log = LogFactory.getLog(Application.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        log.info("IFS Application builder configure method");
        return application.sources(Application.class);
    }

    public static void main(String[] args) throws Exception {
        log.info("IFS boot Application main method");
        SpringApplication.run(Application.class, args);
    }



}