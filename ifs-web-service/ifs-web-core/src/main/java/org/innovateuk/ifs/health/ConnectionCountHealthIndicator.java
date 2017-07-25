package org.innovateuk.ifs.health;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.metrics.ConnectionCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ConnectionCountHealthIndicator implements HealthIndicator{
    private static final Log LOG = LogFactory.getLog(ConnectionCountHealthIndicator.class);

    private final ConnectionCountService service;

    @Autowired
    public ConnectionCountHealthIndicator( ConnectionCountService service){
        this.service = service;
    }

    @Override public Health health() {
        LOG.debug("checking connection count health");
        return service.connectionHealthy() ? Health.up().build() : Health.down().build();
    }


}
