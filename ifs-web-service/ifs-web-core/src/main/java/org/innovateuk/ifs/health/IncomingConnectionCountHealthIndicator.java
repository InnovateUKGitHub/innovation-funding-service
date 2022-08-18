package org.innovateuk.ifs.health;

import org.innovateuk.ifs.filter.ConnectionCountFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * healthindicator for keeping track of the incoming connection count
 */

@Component
public class IncomingConnectionCountHealthIndicator implements HealthIndicator{

    private final ConnectionCountFilter countFilter;

    @Autowired
    public IncomingConnectionCountHealthIndicator( ConnectionCountFilter countFilter){
        this.countFilter = countFilter;
    }

    @Override public Health health() {
        Map<String, Integer> details = new HashMap<>();
        details.put("Count", countFilter.getCount().get());
        details.put("Max", countFilter.getMax());

        if(countFilter.canAcceptConnection()){
            return Health.up()
                    .withDetails(details).build();
        } else {
            return Health.down()
                    .withDetails(details).build();
        }
    }
}
