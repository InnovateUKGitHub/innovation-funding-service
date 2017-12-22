package org.innovateuk.ifs.async.generation;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;

/**
 * Tests for the {@link AsyncFuturesGenerator}, the entrypoint and control gate for all the parallelisation to take
 * place on the IFS platform.  All Futures and awaits() need to be generated via this class in order for us to be able
 * to make use of the control mechanisms it gives us (for example, the ability to wait for the completion of all
 * child Futures and their child Futures before proceeding on the main Thread).
 */
public class AsyncFuturesGeneratorIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AsyncFuturesGenerator generator;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * This test asserts that blocks of code executed via {@link AsyncFuturesGenerator#async(org.innovateuk.ifs.util.ExceptionThrowingSupplier)}
     * are executed with our own TaskExecutor.
     */
    @Test
    public void testAsyncWithSupplierExecutedByThreadExecutor() throws ExecutionException, InterruptedException {

        CompletableFuture<Thread> childThreadFuture = generator.async(Thread::currentThread);
        Thread childThread = childThreadFuture.get();

        assertNotSame(Thread.currentThread(), childThread);
        assertThat(childThread.getName(), startsWith("IFS-Async-Executor-"));
    }

    /**
     * This test asserts that blocks of code executed via {@link AsyncFuturesGenerator#async(org.innovateuk.ifs.util.ExceptionThrowingRunnable)}
     * are executed with our own TaskExecutor.
     */
    @Test
    public void testAsyncWithRunnableExecutedByThreadExecutor() throws ExecutionException, InterruptedException {

        List<Thread> childThreadList = new ArrayList<>();
        CompletableFuture<Void> childThreadFuture = generator.async(() -> {childThreadList.add(Thread.currentThread());});
        childThreadFuture.get();

        Thread childThread = childThreadList.get(0);
        assertNotSame(Thread.currentThread(), childThread);
        assertThat(childThread.getName(), startsWith("IFS-Async-Executor-"));
    }

    /**
     * This test asserts that blocks of code executed via {@link AsyncFuturesGenerator#async(org.innovateuk.ifs.util.ExceptionThrowingSupplier)}
     * are executed with our own TaskExecutor.
     */
    @Test
    public void testFutureIsRegisteredWithMainThread() throws ExecutionException, InterruptedException {

        CountDownLatch childThreadLatch = new CountDownLatch(1);
        CountDownLatch controlLatch = new CountDownLatch(1);

        CompletableFuture<Thread> childThread = generator.async(() -> {

            controlLatch.countDown();
            childThreadLatch.await();

            return Thread.currentThread();
        });

        Future<ConcurrentLinkedQueue<RegisteredAsyncFutureDetails>> controlThread = taskExecutor.submit(() -> {

            try {
                controlLatch.await();

                ConcurrentLinkedQueue<RegisteredAsyncFutureDetails> futuresList = new ConcurrentLinkedQueue<>(AsyncFuturesHolder.getFuturesOrInitialise());

                childThreadLatch.countDown();

                return futuresList;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        childThread.get();

        // Assert that whilst childThread has been executing, it is registered with AsyncFuturesHolder as a Future to track.
        // This is done by virtue of using AsyncFuturesGenerator.async() which immediately registers any Futures that are
        // kicked off via its async() and awaitAll() mechanisms.
        ConcurrentLinkedQueue<RegisteredAsyncFutureDetails> futuresWhilstChildThreadIsInFlight = controlThread.get();
        assertEquals(1, futuresWhilstChildThreadIsInFlight.size());

        ConcurrentLinkedQueue<RegisteredAsyncFutureDetails> futuresNowList = AsyncFuturesHolder.getFuturesOrInitialise();
        assertEquals(futuresWhilstChildThreadIsInFlight.iterator().next(), futuresNowList.iterator().next());


    }
}
