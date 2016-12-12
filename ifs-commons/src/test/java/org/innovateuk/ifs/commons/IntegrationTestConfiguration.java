package org.innovateuk.ifs.commons;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"org.innovateuk.ifs"})
public class IntegrationTestConfiguration {

}
