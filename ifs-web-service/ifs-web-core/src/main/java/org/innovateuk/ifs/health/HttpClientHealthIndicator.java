package org.innovateuk.ifs.health;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class HttpClientHealthIndicator implements HealthIndicator {

    @Value("${ifs.web.rest.connections.monitoring.low-pool-warning-count:5}")
    private int lowPoolWarningCount;

    @Autowired
    private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

    @Override
    public Health health() {
        PoolStats poolStats = poolingHttpClientConnectionManager.getTotalStats();
        logWarning(poolStats);
        Map<String, Integer> stats = getStats(poolStats);
        if (getIsShutdown()) {
            return Health.down().withDetails(stats).build();
        } return Health.up().withDetails(stats).build();
    }

    private void logWarning(PoolStats poolStats) {
        if (poolStats.getPending() > lowPoolWarningCount | poolStats.getLeased() + lowPoolWarningCount > poolStats.getMax()) {
            log.warn(poolStats.toString());
        }
        log.trace(poolStats.toString());
    }

    private boolean getIsShutdown() {
        Field isShutDownField = ReflectionUtils.findField(PoolingHttpClientConnectionManager.class, "isShutDown");
        ReflectionUtils.makeAccessible(isShutDownField);
        AtomicBoolean isShutdown = (AtomicBoolean) ReflectionUtils.getField(isShutDownField, poolingHttpClientConnectionManager);
        if (isShutdown.get()) {
            log.error("Connection pool has been terminated by the HttpClient Library");
        }
        return isShutdown.get();
    }

    private Map<String, Integer> getStats(PoolStats poolStats) {
        Map<String, Integer> details = new HashMap<>();
        details.put("Available", poolStats.getAvailable());
        details.put("Leased", poolStats.getLeased());
        details.put("Pending", poolStats.getPending());
        details.put("Max", poolStats.getMax());
        return details;
    }
}
