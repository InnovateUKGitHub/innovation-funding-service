package org.innovateuk.ifs.metrics;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * class for keeping track of the outgoing connections
 */

@Service
@Slf4j
public class ConnectionCountService {

    private final PoolingHttpClientConnectionManager connManager;

    @Autowired
    public ConnectionCountService(PoolingHttpClientConnectionManager connManager) {
        this.connManager = connManager;
    }

    public boolean connectionHealthy() {
        int max = connManager.getMaxTotal();
        int connections = connManager.getTotalStats().getLeased();
        boolean healthy = max > connections;
        log.trace("outgoing connection used = " + connections + "/" + max + " healthy = "+ healthy);
        return healthy;
    }

}
