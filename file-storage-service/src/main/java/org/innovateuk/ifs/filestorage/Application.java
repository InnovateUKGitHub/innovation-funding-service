package org.innovateuk.ifs.filestorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@Slf4j
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args)  {
        log.info("======== org.innovateuk.ifs.Application.main()");
        SpringApplication.run(Application.class, args);
    }
}
