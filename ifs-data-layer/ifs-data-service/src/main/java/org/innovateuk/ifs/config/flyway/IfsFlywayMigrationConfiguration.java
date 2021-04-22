package org.innovateuk.ifs.config.flyway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConditionalOnProperty(value = "ifs.flyway.repair-on-migrate", havingValue = "true")
public class IfsFlywayMigrationConfiguration {

    private Logger logger = LoggerFactory.getLogger(IfsFlywayMigrationConfiguration.class);

    @Bean
    @Primary
    public FlywayMigrationStrategy cleanMigrateStrategy() {
        logger.info("ifs.flyway.repair-on-migrate is true so applying IfsFlywayMigrationConfiguration");
        return flyway -> {
            logger.info("ifs.flyway.repair-on-migrate=true so running repair...");
            flyway.repair();
            logger.info("... repair done");
            logger.info("ifs.flyway.repair-on-migrate=true so running migrate...");
            flyway.migrate();
            logger.info("... migrate done");
        };
    }

}
