package org.innovateuk.ifs.async.config;

import org.innovateuk.ifs.async.executor.AsyncTaskDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuration for asynchronous execution threads that are spawned from main request processing threads
 * (the AJP threads), and threads spawned from these asynchronous execution threads as well
 */
@Configuration
public class AsyncThreadPoolTaskExecutorConfig {

    @Autowired
    private AsyncTaskDecorator asyncTaskDecorator;

    @Value("${ifs.web.ajp.connections.max.total}")
    private int maxConnections;

    @Value("${ifs.web.max.async.threads}")
    private int maxAsyncThreads;

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setTaskDecorator(asyncTaskDecorator);
        executor.setCorePoolSize(maxConnections);
        executor.setMaxPoolSize(maxAsyncThreads);
        executor.setQueueCapacity(Integer.MAX_VALUE);
        executor.setKeepAliveSeconds(1);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setThreadNamePrefix("IFS-Async-Executor-");
        executor.initialize();
        return executor;
    }
}
