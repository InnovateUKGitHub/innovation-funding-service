package org.innovateuk.ifs.health;

import org.innovateuk.ifs.metrics.ConnectionCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * healthindicator for keeping track of the outgoing connection count
 */

@Component
public class OutgoingConnectionCountHealthIndicator implements HealthIndicator{

    private final ConnectionCountService countService;

    @Autowired
    public OutgoingConnectionCountHealthIndicator( ConnectionCountService countService){
        this.countService = countService;
    }

    @Override public Health health() {
        if(countService.connectionHealthy()){
            return Health.up().build();
        } else {
            return Health.down().build();
        }
    }


}
