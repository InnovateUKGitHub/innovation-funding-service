package org.innovateuk.ifs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    private static final Log LOGGER = LogFactory.getLog(Application.class);


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        LOGGER.info("Spring Application builder configure method");
        LOGGER.info("======== org.innovateuk.ifs.Application.configure()");
        return application.sources(Application.class);
    }
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        LOGGER.info("======== org.innovateuk.ifs.Application.onStartup()");
    }

    public static void main(String[] args) {
        LOGGER.info("======== org.innovateuk.ifs.Application.main()");
        SpringApplication.run(Application.class, args);
    }
}
