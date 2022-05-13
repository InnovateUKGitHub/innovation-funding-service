package ifs.test;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.starter.feign.filestorage.v1.feign.FileUploadFeign;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackageClasses = FileUploadFeign.class)
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args)  {
        log.info("======== org.innovateuk.ifs.Application.main()");
        SpringApplication.run(Application.class, args);
    }
}
