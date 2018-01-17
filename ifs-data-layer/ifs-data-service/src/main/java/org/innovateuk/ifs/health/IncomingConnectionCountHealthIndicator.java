package org.innovateuk.ifs.health;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.filter.ConnectionCountFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * healthindicator for keeping track of the incoming connection count
 */

@Component
public class IncomingConnectionCountHealthIndicator implements HealthIndicator{

    private static final Log LOG = LogFactory.getLog(IncomingConnectionCountHealthIndicator.class);

    private final ConnectionCountFilter countFilter;

    @Autowired
    public IncomingConnectionCountHealthIndicator(ConnectionCountFilter countFilter){
        this.countFilter = countFilter;
    }

    @Override public Health health() {
        if(countFilter.canAcceptConnection()){
            return Health.up().build();
        } else {
            LOG.warn("Cannot accept more incoming connections - reporting this service as unavailable");
            return Health.outOfService().build();
        }
    }


}
