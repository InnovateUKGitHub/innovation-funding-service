package org.innovateuk.ifs.async.config;

import org.innovateuk.ifs.async.executor.AsyncExecutorFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.sleuth.instrument.async.LazyTraceExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * An implementation of {@link AsyncExecutorFactory} that returns a Sleuth-enabled LazyTraceExecutor that wraps a
 * configured ThreadPoolTaskExecutor (as created in {@link AsyncThreadPoolTaskExecutorConfig}) when Sleuth is enabled.
 *
 * This allows Sleuth to correctly correlate asynchronous execution threads with their parent threads so that a
 * proper call hierarchy is produced in Zipkin.
 */
@Component
@ConditionalOnProperty(name = "spring.sleuth.enabled", havingValue = "true")
public class SleuthExecutorFactory implements AsyncExecutorFactory {

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private BeanFactory beanFactory;

    /**
     * @return A LazyTraceExecutor, in order to allow Sleuth to link child Thread executions (Spans) to a parent
     * Thread execution (Span).  In this way, Zipkin can display asynchronous blocks of code under the originating
     * parent Thread and can perform this recursively too.
     */
    @Override
    public Executor createAsyncExecutor() {
        return new LazyTraceExecutor(beanFactory, threadPoolTaskExecutor);
    }
}
