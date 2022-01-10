package org.innovateuk.ifs.health;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.filter.ConnectionCountFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * healthindicator for keeping track of the incoming connection count
 */
@Slf4j
@Component
public class IncomingConnectionCountHealthIndicator implements HealthIndicator{

    private final ConnectionCountFilter countFilter;

    @Autowired
    public IncomingConnectionCountHealthIndicator(ConnectionCountFilter countFilter){
        this.countFilter = countFilter;
    }

    @Override public Health health() {
        if(countFilter.canAcceptConnection()){
            return Health.up().build();
        } else {
            log.warn("Cannot accept more incoming connections - reporting this service as unavailable");
            return Health.outOfService().build();
        }
    }


}
