package org.innovateuk.ifs.metrics;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${ifs.web.async.max.thread}")
    private int maxThread;

    @Override
    public Health health() {

        int activeExecutorThreads = executor.getActiveCount();
        int poolSize = executor.getPoolSize();
        int maxPoolSize = executor.getMaxPoolSize();

        LOG.debug(activeExecutorThreads + " / " + poolSize + " active executor threads - max pool size " + maxPoolSize);

<<<<<<< HEAD
        if ((maxPoolSize - activeExecutorThreads) > maxThread) {
=======
        if ((maxPoolSize - activeExecutorThreads) > 100) {
>>>>>>> origin/development
            return Health.up().build();
        } else {
            LOG.warn("Running out of available async executor threads - reporting this service as unavailable");
            return Health.outOfService().build();
        }
    }
}
