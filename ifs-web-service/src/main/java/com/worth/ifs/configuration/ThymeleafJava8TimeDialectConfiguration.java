package com.worth.ifs.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

@Configuration
class ThymeleafJava8TimeDialectConfiguration {

    @Bean
    public Java8TimeDialect java8TimeDialectDialect() {
        return new Java8TimeDialect();
    }

}
