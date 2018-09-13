package org.innovateuk.ifs.metrics;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * Health indicator for keeping track of the executor thread pool size as used by @Async execution blocks
 */
@Component
public class AsyncExecutionConnectionCountHealthIndicator implements HealthIndicator {

    private static final Log LOG = LogFactory.getLog(AsyncExecutionConnectionCountHealthIndicator.class);

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Override
    public Health health() {

        int activeExecutorThreads = executor.getActiveCount();
        int poolSize = executor.getPoolSize();
        int maxPoolSize = executor.getMaxPoolSize();
        int largestPoolSize = executor.getThreadPoolExecutor().getLargestPoolSize();

        LOG.debug(activeExecutorThreads + " / " + poolSize + " active executor threads - max pool size " + maxPoolSize);

        // TODO DW - 100 is an arbitrary amount - should be based on some average or maximum number of threads that a
        // Controller can produce concurrently whilst doing some work
        if ((maxPoolSize - activeExecutorThreads) > largestPoolSize) {
            return Health.up().build();
        } else {
            LOG.warn("Running out of available async executor threads - reporting this service as unavailable");
            return Health.outOfService().build();
        }
    }
}
