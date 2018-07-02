package org.innovateuk.ifs.testdata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * Configuration for {@link BaseGenerateTestData} and its associated subclasses.  This provides an Executor for
 * executing web test generation in either single- or multi-threaded mode.
 */
@TestConfiguration
public class GenerateTestDataConfiguration {

    private enum ExecutionPolicy {

        // better for generating the final set of data - leads to more consistent ordering and ids
        SINGLE_THREADED,

        // better for during development - significant speedup
        MULTI_THREADED
    }

    @Value("${ifs.generate.test.data.execution:MULTI_THREADED}")
    private ExecutionPolicy executionPolicy;

    @Bean(name = "generateTestDataExecutor")
    public Executor threadPoolTaskExecutor() {

        if (ExecutionPolicy.SINGLE_THREADED == executionPolicy) {
            return singleThreadedExecutor();
        } else {
            return multiThreadedExecutor();
        }
    }

    /**
     * Creates an Executor that {@link BaseGenerateTestData} can use to perform data generation work in parallel.
     */
    private Executor multiThreadedExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(Integer.MAX_VALUE);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setThreadNamePrefix("IFS-Test-Executor-");
        executor.initialize();
        return executor;
    }

    /**
     * Creates an Executor that {@link BaseGenerateTestData} can use to perform data generation work in series.
     *
     * We can't use a "real" Executor with a single Thread, because some of the work would be performed by the
     * single Executor Thread, and some would be performed by the Test worker Thread, thus leading to non-sequential
     * behaviour.  This is down to how some of the CompletableFuture behaviour works, in that either Thread could do
     * some work.
     *
     * By using a mock Executor, all of the work is actually performed by the Test worker Thread, despite
     * BaseGenerateTestData thinking that it is dealing with an Executor.  This way, we don't have any need to specify
     * different code for running in series and running in parallel.
     */
    private Executor singleThreadedExecutor() {
        Executor executor = mock(Executor.class);
        doAnswer(invocation -> {
            Runnable runnable = (Runnable) invocation.getArguments()[0];
            runnable.run();
            return null;
        }).when(executor).execute(isA(Runnable.class));
        return executor;
    }
}
