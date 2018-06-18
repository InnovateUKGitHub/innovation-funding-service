package org.innovateuk.ifs.async.config;

import org.innovateuk.ifs.async.executor.AsyncExecutorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * An implementation of {@link AsyncExecutorFactory} that simply returns a configured ThreadPoolTaskExecutor (as
 * created in {@link AsyncThreadPoolTaskExecutorConfig}) when Sleuth is disabled.
 */
@Component
@ConditionalOnProperty(name = "spring.sleuth.enabled", havingValue = "false")
public class DefaultExecutorFactory implements AsyncExecutorFactory {

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * @return a configured ThreadPoolTaskExecutor
     */
    public Executor createAsyncExecutor() {
        return threadPoolTaskExecutor;
    }
}
