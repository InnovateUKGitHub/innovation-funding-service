package org.innovateuk.ifs.metrics;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConnectionCountService {

    private final PoolingHttpClientConnectionManager connManager;

    @Autowired
    public ConnectionCountService(PoolingHttpClientConnectionManager connManager) {
        this.connManager = connManager;
    }

    public boolean connectionHealthy() {
        return connManager.getMaxTotal() > getUsedConnections();
    }

    private int getUsedConnections(){
        return connManager.getTotalStats().getLeased();
    }

}
