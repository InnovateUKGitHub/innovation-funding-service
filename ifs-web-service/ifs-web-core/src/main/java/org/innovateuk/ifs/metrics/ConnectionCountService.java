package org.innovateuk.ifs.metrics;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConnectionCountService {
    private static final Log LOG = LogFactory.getLog(ConnectionCountService.class);
    private final PoolingHttpClientConnectionManager connManager;

    @Autowired
    public ConnectionCountService(PoolingHttpClientConnectionManager connManager) {
        this.connManager = connManager;
    }

    public boolean connectionHealthy() {
        boolean healthy = connManager.getMaxTotal() > getUsedConnections();
        LOG.info("connection healthy = "+ healthy);
        return healthy;
    }

    private int getUsedConnections(){
        int connections = connManager.getTotalStats().getLeased();
        LOG.info(connections + " connections used");
        return connections;
    }

}
