package org.innovateuk.ifs.metrics;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * class for keeping track of the outgoing connections
 */

@Service
public class ConnectionCountService {
    private static final Log LOG = LogFactory.getLog(ConnectionCountService.class);
    private final PoolingHttpClientConnectionManager connManager;

    @Autowired
    public ConnectionCountService(PoolingHttpClientConnectionManager connManager) {
        this.connManager = connManager;
    }

    public boolean connectionHealthy() {
        int max = connManager.getMaxTotal();
        int connections = connManager.getTotalStats().getLeased();
        boolean healthy = max > connections;
        LOG.debug("outgoing connection used = " + connections + "/" + max + " healthy = "+ healthy);
        return healthy;
    }

}
