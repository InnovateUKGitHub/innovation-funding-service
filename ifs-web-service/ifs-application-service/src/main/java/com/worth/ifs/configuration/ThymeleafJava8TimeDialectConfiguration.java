package com.worth.ifs.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

/**
 * This class is needed to add support for Java 8 new DateTime objects in Thymeleaf.
 * The dialect is needed when you want to be able to format a LocalDateTime object in thymeleaf.
 * {@link java.time.LocalDateTime}
 * @see <a href="https://github.com/thymeleaf/thymeleaf-extras-java8time">Thymeleaf - Java 8</a>
 *
 */
@Configuration
class ThymeleafJava8TimeDialectConfiguration {

    @Bean
    public Java8TimeDialect java8TimeDialectDialect() {
        return new Java8TimeDialect();
    }

}
