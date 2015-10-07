package com.worth.ifs;

import com.worth.ifs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    @Autowired
    UserRepository repository;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        System.out.println("Spring Application builder configure method");
        return application.sources(Application.class);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Spring boot Application main method");
        SpringApplication.run(Application.class, args);
    }
}