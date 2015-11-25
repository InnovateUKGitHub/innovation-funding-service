package com.worth.ifs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.*;

@Configuration
@EnableHypermediaSupport(type = HAL)
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    Log log = LogFactory.getLog(Application.class);


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        System.out.println("Spring Application builder configure method");
        log.info("======== Application.configure()");
        return application.sources(Application.class);
    }
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        log.info("======== Application.onStartup()");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("======== Application.main()");
        SpringApplication.run(Application.class, args);
    }
}