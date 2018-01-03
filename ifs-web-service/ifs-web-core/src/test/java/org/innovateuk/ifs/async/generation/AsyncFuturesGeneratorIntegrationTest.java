package org.innovateuk.ifs.async.generation;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.startsWith;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;
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

    @Before
    public void clearRegisteredFutures() {
        AsyncFuturesHolder.clearFutures();
    }
    
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

        Thread mainThread = Thread.currentThread();

        CountDownLatch childThreadLatch = new CountDownLatch(1);
        CountDownLatch controlLatch = new CountDownLatch(1);

        generator.async(() -> {

            controlLatch.countDown();
            childThreadLatch.await();

            return Thread.currentThread();
        });

        Future<ConcurrentLinkedQueue<RegisteredAsyncFutureDetails>> controlThread = taskExecutor.submit(() -> {

            try {
                // wait for the other Future to be executing before continuing
                controlLatch.await();

                // now that the other Future is executing but not yet complete, check to see that it is registered with
                // AsyncFuturesHolder
                ConcurrentLinkedQueue<RegisteredAsyncFutureDetails> futuresList = new ConcurrentLinkedQueue<>(AsyncFuturesHolder.getFuturesOrInitialise());

                childThreadLatch.countDown();

                return futuresList;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Assert that whilst childThread has been executing, it is registered with AsyncFuturesHolder as a Future to track.
        // This is done by virtue of using AsyncFuturesGenerator.async() which immediately registers any Futures that are
        // kicked off via its async() and awaitAll() mechanisms.
        ConcurrentLinkedQueue<RegisteredAsyncFutureDetails> futuresWhilstChildThreadIsInFlight = controlThread.get();
        assertEquals(1, futuresWhilstChildThreadIsInFlight.size());

        // Assert that the list of Futures recorded mid-flight is the same as the list of Futures recorded on the main Thread
        // after the Futures have completed.
        ConcurrentLinkedQueue<RegisteredAsyncFutureDetails> futuresNowList = AsyncFuturesHolder.getFuturesOrInitialise();
        RegisteredAsyncFutureDetails futureNowItem = getOnlyElement(futuresNowList);

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

        CompletableFuture<List<Thread>> childThread = generator.async(() -> {

            assertEquals(1, AsyncFuturesHolder.getFuturesOrInitialise().size());

            CompletableFuture<Thread> childThreadChild1 = generator.async(Thread::currentThread);

            assertEquals(2, AsyncFuturesHolder.getFuturesOrInitialise().size());

            CompletableFuture<Thread> childThreadChild2 = generator.async(Thread::currentThread);

            assertEquals(3, AsyncFuturesHolder.getFuturesOrInitialise().size());

            Thread childThreadChild1Thread = childThreadChild1.get();
            Thread childThreadChild2Thread = childThreadChild2.get();

            return asList(Thread.currentThread(), childThreadChild1Thread, childThreadChild2Thread);
        });

        List<Thread> futureThreads = childThread.get();

        // assert that the 3 futures were executed by 3 distinct Threads
        assertEquals(3, removeDuplicates(futureThreads).size());

        ConcurrentLinkedQueue<RegisteredAsyncFutureDetails> futuresNowList = AsyncFuturesHolder.getFuturesOrInitialise();

        // assert that the child Futures and its child Futures were all registered
        List<RegisteredAsyncFutureDetails> registeredFuturesAsList = new ArrayList<>(futuresNowList);
        assertEquals(3, registeredFuturesAsList.size());

        RegisteredAsyncFutureDetails childThreadDetails = registeredFuturesAsList.get(0);
        RegisteredAsyncFutureDetails childThreadChild1Details = registeredFuturesAsList.get(1);
        RegisteredAsyncFutureDetails childThreadChild2Details = registeredFuturesAsList.get(2);

        // assert that the child Future's own child Futures retain a full ancestry of the Threads that initiated them,
        // back to the main "Top level" Thread
        List<String> childFutureNameAndTopLevel = asList(childThreadDetails.getFutureName(), "Top level");
        assertEquals(childFutureNameAndTopLevel, childThreadChild1Details.getFutureAncestry());
        assertEquals(childFutureNameAndTopLevel, childThreadChild2Details.getFutureAncestry());
    }
}
