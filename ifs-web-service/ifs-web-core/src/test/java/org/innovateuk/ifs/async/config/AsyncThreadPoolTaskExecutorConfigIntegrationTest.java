package org.innovateuk.ifs.async.config;

import org.innovateuk.ifs.async.AsyncExecutionTestHelper;
import org.innovateuk.ifs.async.executor.AsyncTaskDecorator;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the configuration of our {@link AsyncThreadPoolTaskExecutorConfig} configuration that allows us
 * to specify our own {@link java.util.concurrent.ThreadPoolExecutor} for the execution of @Async code blocks
 */
public class AsyncThreadPoolTaskExecutorConfigIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AsyncExecutionTestHelper helper;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * This test is just a sanity check that we have autowired the correct task executor
     */
    @Test
    public void testThreadPoolTaskExecutorIsCorrectOne() {
        assertEquals("IFS-Async-Executor-", threadPoolTaskExecutor.getThreadNamePrefix());
        assertTrue(ReflectionTestUtils.getField(threadPoolTaskExecutor, "taskDecorator") instanceof AsyncTaskDecorator);
    }

    /**
     * This test asserts that Future code is executed with the Thread Pool configured in
     * {@link org.innovateuk.ifs.async.config.AsyncThreadPoolTaskExecutorConfig} when executed from our
     * {@link AsyncFuturesGenerator#async(org.innovateuk.ifs.util.ExceptionThrowingRunnable) entrypoint}
     *
     */
    @Test
    public void testAsyncJobsAreExecutedByOurAsyncExecutorFactory() throws ExecutionException, InterruptedException {

        CompletableFuture<String> future = helper.executeAsync(() -> Thread.currentThread().getName());
        String futureThreadName = future.get();
        assertThat(futureThreadName, startsWith("IFS-Async-Executor-"));
    }

    /**
     * This test tests that parallel Futures will be invoked in new Threads from the pool
     */
    @Test
    public void testAsyncJobsAreExecutedByOurAsyncExecutorFactoryAndUseDifferentThreads() throws ExecutionException, InterruptedException {

        CompletableFuture<List<String>> future = helper.executeAsync(() -> {
            CompletableFuture<List<String>> future2 = helper.executeAsync(() -> {
                CompletableFuture<String> future3 = helper.executeAsync(() -> Thread.currentThread().getName());
                String futureThreadName = future3.get();
                return asList(Thread.currentThread().getName(), futureThreadName);
            });
            List<String> futureThreadNames = future2.get();
            return combineLists(Thread.currentThread().getName(), futureThreadNames);
        });

        // assert the thread executor threads were used
        List<String> threadNames = future.get();
        threadNames.forEach(threadName -> assertThat(threadName, startsWith("IFS-Async-Executor-")));

        // and assert that it was 3 unique threads from the pool
        assertThat(threadNames, hasSize(3));
        assertThat(removeDuplicates(threadNames), hasSize(3));
    }
}
