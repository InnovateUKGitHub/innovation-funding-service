package com.worth.ifs.commons;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.worth.ifs"})
public class IntegrationTestConfiguration {

}