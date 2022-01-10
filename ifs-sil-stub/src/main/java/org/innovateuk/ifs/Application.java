package org.innovateuk.ifs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@Slf4j
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        log.info("Spring Application builder configure method");
        log.info("======== org.innovateuk.ifs.Application.configure()");
        return application.sources(Application.class);
    }
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        log.info("======== org.innovateuk.ifs.Application.onStartup()");
    }

    public static void main(String[] args) {
        log.info("======== org.innovateuk.ifs.Application.main()");
        SpringApplication.run(Application.class, args);
    }
}
