package org.innovateuk.ifs.metrics;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * Health indicator for keeping track of the executor thread pool size as used by @Async execution blocks
 */
@Slf4j
@Component
public class AsyncExecutionConnectionCountHealthIndicator implements HealthIndicator {

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Value("${ifs.web.async.max.thread}")
    private int maxThread;

    @Override
    public Health health() {

        int activeExecutorThreads = executor.getActiveCount();
        int poolSize = executor.getPoolSize();
        int maxPoolSize = executor.getMaxPoolSize();

        log.trace(activeExecutorThreads + " / " + poolSize + " active executor threads - max pool size " + maxPoolSize);

        if ((maxPoolSize - activeExecutorThreads) > maxThread) {
            return Health.up().build();
        } else {
            log.warn("Running out of available async executor threads - reporting this service as unavailable");
            return Health.outOfService().build();
        }
    }
}
