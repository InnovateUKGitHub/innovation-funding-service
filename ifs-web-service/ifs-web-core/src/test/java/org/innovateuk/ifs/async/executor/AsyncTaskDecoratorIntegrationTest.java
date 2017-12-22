package org.innovateuk.ifs.async.executor;

import org.innovateuk.ifs.async.AsyncExecutionTestHelper;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Tests to ensure that the {@link AsyncTaskDecorator} is being applied successfully to threads that are executing @Async
 * blocks of code.  The main job of the {@link AsyncTaskDecorator} is to transfer important ThreadLocals from a Thread
 * to any child Threads that it initiates via @Async code block execution.
 */
@TestPropertySource(properties = "ifs.web.ajp.connections.max.total=2")
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
            SecurityContext context = mock(SecurityContext.class);
            SecurityContextHolder.setContext(context);
        };

        testThreadLocalTransferredToChildThread(setNewThreadLocalValueFn, SecurityContextHolder::getContext);
    }

    /**
     * This test case asserts that the ThreadLocal value of {@link RequestAttributes} is transferred from the top-level
     * Thread to its child Threads (and their children).
     *
     * This brings over some useful values that are stored for the life of the currently running HTTP Request.  Of
     * particular interest is the UUID generated per-request by {@link org.innovateuk.ifs.cache.RequestUidSupplier}
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

    // this test method runs the ThreadLocal assertion test twice to test the reuse of the Threads in the Thread Pool
    private <T> void testThreadLocalTransferredToChildThread(Runnable setupNewThreadLocalValueFn, Supplier<T> threadLocalGetter) throws InterruptedException, ExecutionException {

        List<Thread> childThreads1 = testThreadLocalTransferredToChildThreadIndividual(setupNewThreadLocalValueFn, threadLocalGetter);
        List<Thread> childThreads2 = testThreadLocalTransferredToChildThreadIndividual(setupNewThreadLocalValueFn, threadLocalGetter);

        // assert that each assertion test used 2 child threads each, but that overall only 2 Threads were actually used
        // because our thread pool size defined at the top of this test is 2
        List<Thread> allChildThreads = combineLists(childThreads1, childThreads2);
        assertEquals(4, allChildThreads.size());
        assertEquals(2, removeDuplicates(allChildThreads).size());
    }

    private <T> List<Thread> testThreadLocalTransferredToChildThreadIndividual(Runnable setupNewThreadLocalValueFn, Supplier<T> threadLocalGetter) throws InterruptedException, ExecutionException {

        setupNewThreadLocalValueFn.run();

        T threadLocalValueOnTopThread = threadLocalGetter.get();

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

        return futureAssertion.get();
    }

}
