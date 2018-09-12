package org.innovateuk.ifs.async.generation;

import org.innovateuk.ifs.async.util.AsyncAllowedThreadLocal;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.Thread.currentThread;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.startsWith;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;
import static org.junit.Assert.*;

/**
 * Tests for the {@link AsyncFuturesGenerator#async} methods, the main entrypoint for all the parallelisation to take
 * place on the IFS platform.  All Futures and awaits() need to be generated via this class in order for us to be able
 * to make use of the control mechanisms it gives us (for example, the ability to wait for the completion of all
 * child Futures and their child Futures before proceeding on the main Thread).
 */
public class AsyncFuturesGeneratorIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AsyncFuturesGenerator generator;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Before
    public void clearRegisteredFutures() {
        AsyncFuturesHolder.clearFutures();
    }

    @Before
    public void enableAsync() {
        AsyncAllowedThreadLocal.setAsyncAllowed(true);
    }

    @After
    public void disableAsync() {
        AsyncAllowedThreadLocal.setAsyncAllowed(false);
    }

    /**
     * This test asserts that blocks of code executed via {@link AsyncFuturesGenerator#async(org.innovateuk.ifs.util.ExceptionThrowingSupplier)}
     * are executed with our own TaskExecutor.
     */
    @Test
    public void testAsyncWithSupplierExecutedByThreadExecutor() throws ExecutionException, InterruptedException {

        CompletableFuture<Thread> childThreadFuture = generator.async(Thread::currentThread);
        Thread childThread = childThreadFuture.get();

        assertNotSame(currentThread(), childThread);
        assertThat(childThread.getName(), startsWith("IFS-Async-Executor-"));
    }

    /**
     * This test asserts that blocks of code executed via {@link AsyncFuturesGenerator#async(org.innovateuk.ifs.util.ExceptionThrowingRunnable)}
     * are executed with our own TaskExecutor.
     */
    @Test
    public void testAsyncWithRunnableExecutedByThreadExecutor() throws ExecutionException, InterruptedException {

        List<Thread> childThreadList = new ArrayList<>();
        CompletableFuture<Void> childThreadFuture = generator.async(() -> {childThreadList.add(currentThread());});
        childThreadFuture.get();

        Thread childThread = childThreadList.get(0);
        assertNotSame(currentThread(), childThread);
        assertThat(childThread.getName(), startsWith("IFS-Async-Executor-"));
    }

    /**
     * This test asserts that blocks of code executed via {@link AsyncFuturesGenerator#async(org.innovateuk.ifs.util.ExceptionThrowingSupplier)}
     * are executed with the main thread if async is disabled.
     */
    @Test
    public void testAsyncWithSupplierExecutedByMainThreadIfAsyncNotAllowed() throws ExecutionException, InterruptedException {

        AsyncAllowedThreadLocal.setAsyncAllowed(false);

        CompletableFuture<Thread> childThreadFuture = generator.async(Thread::currentThread);
        Thread childThread = childThreadFuture.get();

        assertSame(currentThread(), childThread);
    }

    /**
     * This test asserts that blocks of code executed via {@link AsyncFuturesGenerator#async(org.innovateuk.ifs.util.ExceptionThrowingRunnable)}
     * are executed with the main thread if async is disabled.
     */
    @Test
    public void testAsyncWithRunnableExecutedByMainThreadIfAsyncNotAllowed() throws ExecutionException, InterruptedException {

        AsyncAllowedThreadLocal.setAsyncAllowed(false);

        List<Thread> childThreadList = new ArrayList<>();
        CompletableFuture<Void> childThreadFuture = generator.async(() -> {childThreadList.add(currentThread());});
        childThreadFuture.get();

        assertSame(currentThread(), childThreadList.get(0));
    }

    /**
     * This test asserts that blocks of code executed via {@link AsyncFuturesGenerator#async(org.innovateuk.ifs.util.ExceptionThrowingSupplier)}
     * are executed with our own TaskExecutor.
     */
    @Test
    public void testFutureIsRegisteredWithMainThread() throws ExecutionException, InterruptedException {

        Thread mainThread = currentThread();

        CountDownLatch childThreadLatch = new CountDownLatch(1);
        CountDownLatch controlLatch = new CountDownLatch(1);

        generator.async(() -> {

            controlLatch.countDown();
            childThreadLatch.await();

            return currentThread();
        });

        Future<ConcurrentLinkedQueue<RegisteredAsyncFutureDetails<?>>> controlThread = taskExecutor.submit(() -> {

            try {
                // wait for the other Future to be executing before continuing
                controlLatch.await();

                // now that the other Future is executing but not yet complete, check to see that it is registered with
                // AsyncFuturesHolder
                ConcurrentLinkedQueue<RegisteredAsyncFutureDetails<?>> futuresList = new ConcurrentLinkedQueue<>(AsyncFuturesHolder.getFuturesOrInitialise());

                childThreadLatch.countDown();

                return futuresList;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Assert that whilst childThread has been executing, it is registered with AsyncFuturesHolder as a Future to track.
        // This is done by virtue of using AsyncFuturesGenerator.async() which immediately registers any Futures that are
        // kicked off via its async() and awaitAll() mechanisms.
        ConcurrentLinkedQueue<RegisteredAsyncFutureDetails<?>> futuresWhilstChildThreadIsInFlight = controlThread.get();
        assertEquals(1, futuresWhilstChildThreadIsInFlight.size());

        // Assert that the list of Futures recorded mid-flight is the same as the list of Futures recorded on the main Thread
        // after the Futures have completed.
        Queue<RegisteredAsyncFutureDetails<?>> futuresNowList = AsyncFuturesHolder.getFuturesOrInitialise();
        RegisteredAsyncFutureDetails<?> futureNowItem = getOnlyElement(futuresNowList);

        // assert that the registered Future retains the Thread ancestry back to the thread that initiated it
        assertEquals(futuresWhilstChildThreadIsInFlight.iterator().next(), futureNowItem);
        assertEquals(mainThread.getName(), getOnlyElement(futureNowItem.getThreadAncestry()));

        // assert that the single Future has no Future ancestry recorded, as it was initiated directly from the main
        // Thread
        assertEquals(singletonList("Top level"), futureNowItem.getFutureAncestry());
    }

    /**
     * This test asserts that blocks of code executed via {@link AsyncFuturesGenerator#async(org.innovateuk.ifs.util.ExceptionThrowingSupplier)}
     * are executed with our own TaskExecutor and that they can register their child futures also
     */
    @Test
    public void testAllDescendantFuturesAreRegisteredWithMainThread() throws ExecutionException, InterruptedException {

        // this latch is used to ensure that childFuture1 and childFuture2 are executed in 2 distinct Threads (it's
        // perfectly possible for them to both execute after each other in the same Thread)
        CountDownLatch childFuture1Latch = new CountDownLatch(1);

        CompletableFuture<List<Thread>> future = generator.async(() -> {

            assertEquals(1, AsyncFuturesHolder.getFuturesOrInitialise().size());

            CompletableFuture<Thread> childFuture1 = generator.async(() -> {
                childFuture1Latch.await();
                return currentThread();
            });

            assertEquals(2, AsyncFuturesHolder.getFuturesOrInitialise().size());

            CompletableFuture<Thread> childFuture2 = generator.async(() -> {
                childFuture1Latch.countDown();
                return currentThread();
            });

            assertEquals(3, AsyncFuturesHolder.getFuturesOrInitialise().size());

            Thread childFuture1Thread = childFuture1.get();
            Thread childFuture2Thread = childFuture2.get();

            return asList(currentThread(), childFuture1Thread, childFuture2Thread);
        });

        List<Thread> futureThreads = future.get();

        // assert that the 3 futures were executed by 3 distinct Threads
        assertEquals(3, removeDuplicates(futureThreads).size());

        Queue<RegisteredAsyncFutureDetails<?>> futuresNowList = AsyncFuturesHolder.getFuturesOrInitialise();

        // assert that the child Futures and its child Futures were all registered
        List<RegisteredAsyncFutureDetails<?>> registeredFuturesAsList = new ArrayList<>(futuresNowList);
        assertEquals(3, registeredFuturesAsList.size());

        RegisteredAsyncFutureDetails futureDetails = registeredFuturesAsList.get(0);
        RegisteredAsyncFutureDetails childFuture1Details = registeredFuturesAsList.get(1);
        RegisteredAsyncFutureDetails childFuture2Details = registeredFuturesAsList.get(2);

        // assert that the child Future's own child Futures retain a full ancestry of the Threads that initiated them,
        // back to the main "Top level" Thread
        List<String> childFutureNameAndTopLevel = asList(futureDetails.getFutureName(), "Top level");
        assertEquals(childFutureNameAndTopLevel, childFuture1Details.getFutureAncestry());
        assertEquals(childFutureNameAndTopLevel, childFuture2Details.getFutureAncestry());
    }

    /**
     * Test that we can explicitly name Futures, which is useful during debugging
     */
    @Test
    public void testExplicitlyNamingFuturesWorks() throws ExecutionException, InterruptedException {

        CompletableFuture<List<Thread>> future = generator.async("Future", () -> {

            CompletableFuture<Thread> childFuture1 = generator.async("Child Future 1", Thread::currentThread);
            CompletableFuture<Thread> childFuture2 = generator.async("Child Future 2", Thread::currentThread);

            Thread childFuture1Thread = childFuture1.get();
            Thread childFuture2Thread = childFuture2.get();

            return asList(currentThread(), childFuture1Thread, childFuture2Thread);
        });

        future.get();

        Queue<RegisteredAsyncFutureDetails<?>> futuresList = AsyncFuturesHolder.getFuturesOrInitialise();

        // assert that the child Futures and its child Futures were all registered
        List<RegisteredAsyncFutureDetails<?>> registeredFuturesAsList = new ArrayList<>(futuresList);

        RegisteredAsyncFutureDetails<?> futureDetails = registeredFuturesAsList.get(0);
        RegisteredAsyncFutureDetails<?> childFuture1Details = registeredFuturesAsList.get(1);
        RegisteredAsyncFutureDetails<?> childFuture2Details = registeredFuturesAsList.get(2);

        // assert that the explicit names were used when registering our Futures
        assertEquals("Future", futureDetails.getFutureName());
        assertEquals("Child Future 1", childFuture1Details.getFutureName());
        assertEquals("Child Future 2", childFuture2Details.getFutureName());

        // assert that these explicit names are reflected in the Future ancestry
        assertEquals(asList("Future", "Top level"), childFuture1Details.getFutureAncestry());
        assertEquals(asList("Future", "Top level"), childFuture2Details.getFutureAncestry());
    }
}
