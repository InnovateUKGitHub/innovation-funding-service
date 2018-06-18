package org.innovateuk.ifs.async.config;

import org.innovateuk.ifs.async.executor.AsyncExecutorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;

import java.util.concurrent.Executor;

/**
 * Configuration for asynchronous execution threads that are spawned from main request processing threads
 * (the AJP threads), and threads spawned from these asynchronous execution threads as well
 */
@Configuration
public class AsyncExecutionConfig extends AsyncConfigurerSupport {

    @Autowired
    private AsyncExecutorFactory asyncExecutorFactory;

    @Override
    public Executor getAsyncExecutor() {
        return asyncExecutorFactory.createAsyncExecutor();
    }
}
