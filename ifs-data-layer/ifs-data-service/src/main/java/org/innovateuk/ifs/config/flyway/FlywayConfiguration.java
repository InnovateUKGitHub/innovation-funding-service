package org.innovateuk.ifs.config.flyway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FlywayConfiguration {
    @Bean
    public FlywayMigrationStrategy repairMigrateStrategy(DataServiceFlywayConfiguration configuration) {
        return flyway -> {
            log.warn("Flyway repair enabled: " + configuration.isRepair());
            if (configuration.isRepair()) {
                log.warn("Running flyway repair");
                flyway.repair();
                log.warn("Running flyway repair complete");
            }
            flyway.migrate();
        };
    }

}
