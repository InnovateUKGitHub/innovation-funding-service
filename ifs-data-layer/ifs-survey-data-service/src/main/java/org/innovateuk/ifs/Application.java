package org.innovateuk.ifs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

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

    /**
     * DefaultFormattingConversionService registered to allow the injection of properties lists into @Value fields
     */
    @Bean
    public static ConversionService conversionService() {
        return new DefaultFormattingConversionService();
    }

    public static void main(String[] args)  {
        log.info("======== org.innovateuk.ifs.Application.main()");
        SpringApplication.run(Application.class, args);
    }
}
