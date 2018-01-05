package org.innovateuk.ifs.cache;

import com.google.common.cache.Cache;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link RestCacheMethodInterceptor} in respect to thread safety.  This is important when using
 * {@link org.innovateuk.ifs.async.generation.AsyncFuturesGenerator} and its respective mechanisms to execute
 * RestTemplate calls in parallel.  When executing parallel calls under high load, we need to ensure that a
 * single Controller call can safely share cache results and add cache results to the existing set of results
 * between the various Threads its Futures execute within.
 */
public class RestCacheMethodInterceptorThreadSafetyTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private RestCacheMethodInterceptor interceptor;

    @Mock
    private Cache<String, Map<Method, Map<List<Object>, Object>>> cacheMock;

    @Mock
    private MethodInvocation methodInvocationMock;

    @Mock
    private UidSupplier uidSupplierMock;

    @Test
    public void testReadOperationDoesntBlockOtherReadOperations() throws Throwable {

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        CountDownLatch readOperation1Latch = new CountDownLatch(1);
        CountDownLatch readOperation2Latch = new CountDownLatch(1);

        ReadWriteLock lockFromInterceptor =
                (ReadWriteLock) ReflectionTestUtils.getField(interceptor, "lock");

        Object sync = ReflectionTestUtils.getField(lockFromInterceptor.readLock(), "sync");

        when(methodInvocationMock.getArguments()).thenReturn(new Object[] {"id", 123});

        when(uidSupplierMock.get()).thenReturn("http-request-1");

        Queue<Pair<String, Boolean>> successfulCalls = new ConcurrentLinkedQueue<>();

        ThreadLocal<String> namedCallsThreadLocal = new ThreadLocal<>();

        when(cacheMock.get(eq("http-request-1"), isA(Callable.class))).thenAnswer(invocation -> {

            String operationName = namedCallsThreadLocal.get();

            // The first Future call to reach this expectation ("Read Operation 1") will fall into the "if" block
            // and block on the latch, keeping the read lock locked in {@link RestCacheMethodInterceptor#get}
            //
            // The second Future to reach this expectation ("Read Operation 2") will unblock the first.  The fact
            // that the second could make it this far shows that it was able to get through the read lock in
            // {@link RestCacheMethodInterceptor#get}.
            if ("Read Operation 1".equals(operationName) && !successfulCalls.contains("Read Operation 1")) {
                successfulCalls.add(Pair.of(operationName, testLock(sync)));
                readOperation2Latch.countDown();
                readOperation1Latch.await();
            } else {
                readOperation1Latch.countDown();
                successfulCalls.add(Pair.of(namedCallsThreadLocal.get(), testLock(sync)));
            }

            return new HashMap<>();
        });

        Future<Object> readOperation1Future = executorService.submit(() -> {
            try {
                namedCallsThreadLocal.set("Read Operation 1");
                return interceptor.invoke(methodInvocationMock);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        Future<Object> readOperation2Future = executorService.submit(() -> {
            try {
                readOperation2Latch.await();
                namedCallsThreadLocal.set("Read Operation 2");
                return interceptor.invoke(methodInvocationMock);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        readOperation1Future.get();
        readOperation2Future.get();

        // assert that both operations were successful
        List<String> successfulOperationNames = removeDuplicates(simpleMap(successfulCalls, Pair::getLeft));
        assertThat(successfulOperationNames, containsInAnyOrder("Read Operation 1", "Read Operation 2"));

        // assert that the read lock was always on when the 2 calls were inside the bounds of
        // {@link RestCacheMethodInterceptor#get}
        List<Boolean> successfulOperationLockStates = simpleMap(successfulCalls, Pair::getRight);
        assertFalse(successfulOperationLockStates.stream().anyMatch(state -> !state));
    }

    private boolean testLock(Object sync) {
        return ((Integer) ReflectionTestUtils.invokeGetterMethod(sync, "readLockCount")) > 0;
    }


//    @Test
//    public void testReadOperationDoesntBlockOtherReadOperations() throws Throwable {
//
//        CountDownLatch blockingLatch = new CountDownLatch(1);
//
//        when(methodInvocationMock.getArguments()).thenReturn(new Object[] {"id", 123});
//
//        when(uidSupplierMock.get()).thenReturn("http-request-1");
//
//        when(cacheMock.get(eq("http-request-1"), isA(Callable.class))).thenReturn(new HashMap<>());
//
//        doAnswer(invocation -> {
//            blockingLatch.await();
//            return null;
//        }).when(cacheMock).put("", null);
//
//        interceptor.invoke(methodInvocationMock);
//    }
}
