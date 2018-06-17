package org.innovateuk.ifs.async.executor;

import org.innovateuk.ifs.async.AsyncExecutionTestHelper;
import org.innovateuk.ifs.async.util.AsyncAllowedThreadLocal;
import org.innovateuk.ifs.async.controller.AwaitModelFuturesCompletionMethodInterceptor;
import org.innovateuk.ifs.async.generation.AsyncFuturesHolder;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Tests to ensure that the {@link AsyncTaskDecorator} is being applied successfully to threads that are executing @Async
 * blocks of code.  The main job of the {@link AsyncTaskDecorator} is to transfer important ThreadLocals from a Thread
 * to any child Threads that it initiates via @Async code block execution.
 */
public class AsyncTaskDecoratorIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AsyncExecutionTestHelper helper;

    /**
     * This test case asserts that the ThreadLocal value of {@link SecurityContext} is transferred from the top-level
     * Thread to its child Threads (and their children).
     *
     * This gives us the ability to determine who the current User is which enables us to make REST calls to the
     * data layer as the currently provisioned User in child Threads.  Without this, the calls to the data layer would
     * appear to be "anonymous".
     */
    @Test
    public void testSpringSecurityContextTransferredToChildThreads() throws ExecutionException, InterruptedException {

        Runnable setNewThreadLocalValueFn = () -> {
            Authentication authentication = mock(Authentication.class);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        };

        testThreadLocalTransferredToChildThread(setNewThreadLocalValueFn,
                () -> SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * This test case asserts that the ThreadLocal value of {@link RequestAttributes} is transferred from the top-level
     * Thread to its child Threads (and their children).
     *
     * This brings over some useful values that are stored for the life of the currently running HTTP Request.  Of
     * particular interest is the UUID generated per-request by {@link org.innovateuk.ifs.cache.RestCachePerRequestUuidSupplier}
     * which enables any REST calls from child Threads (and their children) to be able to reuse, and feed back into, the
     * Rest Result caching that takes part in {@link org.innovateuk.ifs.cache.RestCacheMethodInterceptor}, effectively
     * letting the main Thread and its descendant Threads all share each other's cached results for the duration of the
     * Request's life.
     */
    @Test
    public void testRequestAttributesTransferredToChildThreads() throws ExecutionException, InterruptedException {

        Runnable setNewThreadLocalValueFn = () -> {
            RequestAttributes attributes = mock(RequestAttributes.class);
            RequestContextHolder.setRequestAttributes(attributes);
        };

        testThreadLocalTransferredToChildThread(setNewThreadLocalValueFn, RequestContextHolder::getRequestAttributes);
    }

    /**
     * This test case asserts that the ThreadLocal value in {@link AsyncFuturesHolder} is transferred from the top-level
     * Thread to its child Threads (and their children).
     *
     * This transfers a List in which we keep a track of all Futures and children of those Futures that were spawned
     * from the main Thread, so that we can keep a track of every Future that has (or will be) launched from the main
     * Thread, so that we can choose to wait for all to complete before letting the main Thread continue, as we do in
     * {@link AwaitModelFuturesCompletionMethodInterceptor} to ensure that all Futures have
     * completed populating the Spring Model before passing to Thymeleaf to render.
     */
    @Test
    public void testFuturesListTransferredToChildThreads() throws ExecutionException, InterruptedException {

        Runnable setNewThreadLocalValueFn = () -> AsyncFuturesHolder.setFutures(new ConcurrentLinkedQueue<>());

        testThreadLocalTransferredToChildThread(setNewThreadLocalValueFn, AsyncFuturesHolder::getFuturesOrInitialise);
    }

    /**
     * This test case asserts that the ThreadLocal value of
     * {@link AsyncAllowedThreadLocal} is transferred from the top-level
     * Thread to its child Threads (and their children).
     *
     * This gives us the ability to know that the currentlyexecuting Thread was generated from a code block
     * that explicitly allows async behaviour to occur.
     */
    @Test
    public void testAsyncAllowedTransferredToChildThreads() throws ExecutionException, InterruptedException {

        Runnable setNewThreadLocalValueFn = () -> AsyncAllowedThreadLocal.setAsyncAllowed(true);

        testThreadLocalTransferredToChildThread(setNewThreadLocalValueFn,
                AsyncAllowedThreadLocal::isAsyncAllowed);

        AsyncAllowedThreadLocal.clearAsyncAllowed();
    }

    /**
     * This test case asserts that we are selectively choosing ThreadLocals to copy across rather than moving them all.
     */
    @Test
    public void testArbitraryThreadLocalsNotTransferredToChildThreads() throws ExecutionException, InterruptedException {

        ThreadLocal<Integer> arbitraryThreadLocal = new ThreadLocal<>();
        arbitraryThreadLocal.set(123);

        CompletableFuture<Integer> futureAssertion = helper.executeAsync(() -> arbitraryThreadLocal.get());
        Integer childValue = futureAssertion.get();
        assertNull(childValue);
    }

    // this test method runs the ThreadLocal assertion test multiple times to test the reuse of the Threads in the Thread Pool
    private <T> void testThreadLocalTransferredToChildThread(Runnable setupNewThreadLocalValueFn, Supplier<T> threadLocalGetter) throws InterruptedException, ExecutionException {

        List<List<Thread>> childThreads = range(0, 20).mapToObj(i -> testThreadLocalTransferredToChildThreadIndividual(setupNewThreadLocalValueFn, threadLocalGetter)).collect(toList());

        // assert that each assertion test used 2 child threads each, but that overall only 2 Threads were actually used
        // because our thread pool size defined at the top of this test is 2
        List<Thread> allChildThreads = flattenLists(childThreads);
        assertEquals(40, allChildThreads.size());
        assertEquals(10, removeDuplicates(allChildThreads).size());
    }

    private <T> List<Thread> testThreadLocalTransferredToChildThreadIndividual(Runnable setupNewThreadLocalValueFn, Supplier<T> threadLocalGetter) {

        setupNewThreadLocalValueFn.run();

        T threadLocalValueOnTopThread = threadLocalGetter.get();

        assertNotNull(threadLocalValueOnTopThread);

        Thread topThread = Thread.currentThread();

        CompletableFuture<List<Thread>> futureAssertion = helper.executeAsync(() -> {

            assertNotSame(topThread, Thread.currentThread());

            assertSame(threadLocalValueOnTopThread, threadLocalGetter.get());

            CompletableFuture<Thread> futureAssertion2 = helper.executeAsync(() -> {
                assertNotSame(topThread, Thread.currentThread());
                assertSame(threadLocalValueOnTopThread, threadLocalGetter.get());
                return Thread.currentThread();
            });

            Thread childChildThread = futureAssertion2.get();

            return asList(Thread.currentThread(), childChildThread);
        });

        try {
            return futureAssertion.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
