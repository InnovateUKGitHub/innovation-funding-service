package org.innovateuk.ifs.cache;

import com.google.common.cache.Cache;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.innovateuk.ifs.BaseUnitTest;
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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.innovateuk.ifs.async.AsyncExecutionTestHelper.BLOCKING_TIMEOUT_MILLIS;
import static org.innovateuk.ifs.async.ReadWriteLockTestHelper.isReadLocked;
import static org.innovateuk.ifs.async.ReadWriteLockTestHelper.isWriteLocked;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.*;
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
public class RestCacheMethodInterceptorThreadSafetyTest extends BaseUnitTest {

    @InjectMocks
    private RestCacheMethodInterceptor interceptor;

    @Mock
    private Cache<String, Map<Method, Map<List<Object>, Object>>> cacheMock;

    @Mock
    private MethodInvocation methodInvocationMock;

    @Mock
    private RestCacheUuidSupplier uidSupplierMock;

    /**
     * This test sets in motion 2 parallel read operations on the cache and shows that the first doesn't block the
     * second.  This is achieved by blocking the first operation within the boundaries of the read lock so that it is
     * holding a read lock open and then allowing the second operation to continue on into the read lock boundary itself.
     * That will prove that 2 read operations can be within the bounds of the read lock at the same time.  The second
     * operation can then unblock the first read operation.
     */
    @Test
    public void testReadOperationDoesntBlockOtherReadOperations() throws Throwable {

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        CountDownLatch readOperation1Latch = new CountDownLatch(1);
        CountDownLatch readOperation2Latch = new CountDownLatch(1);

        ReadWriteLock lockFromInterceptor =
                (ReadWriteLock) ReflectionTestUtils.getField(interceptor, "lock");

        when(methodInvocationMock.getArguments()).thenReturn(new Object[] {"id", 123});

        when(uidSupplierMock.get()).thenReturn("http-request-1");

        Queue<Pair<String, Boolean>> successfulCalls = new ConcurrentLinkedQueue<>();

        ThreadLocal<String> namedCallsThreadLocal = new ThreadLocal<>();

        when(cacheMock.get(eq("http-request-1"), isA(Callable.class))).thenAnswer(invocation -> {

            // for this test, we're only interested in capturing details of when both read operations are performing
            // their initial "gets".  cache.get() is called during a read operation after the result has been read
            // and now needs to be stored for caching.  We're not interested in these interactions with the cache, only
            // the original reads
            if (!isWriteLocked(lockFromInterceptor)) {

                String operationName = namedCallsThreadLocal.get();

                // The first Future call to reach this expectation ("Read Operation 1") will fall into the "if" block
                // and block on the latch, keeping the read lock locked in {@link RestCacheMethodInterceptor#get}
                //
                // The second Future to reach this expectation ("Read Operation 2") will unblock the first.  The fact
                // that the second could make it this far shows that it was able to get through the read lock in
                // {@link RestCacheMethodInterceptor#get}.
                if ("Read Operation 1".equals(operationName) && !successfulCalls.contains("Read Operation 1")) {
                    successfulCalls.add(Pair.of(operationName, isReadLocked(lockFromInterceptor)));
                    readOperation2Latch.countDown();
                    readOperation1Latch.await();
                } else {
                    readOperation1Latch.countDown();
                    successfulCalls.add(Pair.of(namedCallsThreadLocal.get(), isReadLocked(lockFromInterceptor)));
                }
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
        List<String> successfulOperationNames = simpleMap(successfulCalls, Pair::getLeft);
        assertThat(successfulOperationNames, contains("Read Operation 1", "Read Operation 2"));

        // assert that the read lock was always on when the 2 calls were inside the bounds of
        // {@link RestCacheMethodInterceptor#get}
        List<Boolean> successfulOperationLockStates = simpleMap(successfulCalls, Pair::getRight);
        assertTrue(successfulOperationLockStates.stream().allMatch(state -> state));
    }

    /**
     * This test asserts that an operation holding the read lock in the {@link RestCacheMethodInterceptor#get} method
     * will block a second operation from being able to write its results to the cache.
     */
    @Test
    public void testReadOperationBlocksWriteOperations() throws Throwable {

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        CountDownLatch readOperationLatch = new CountDownLatch(1);
        CountDownLatch writeOperationLatch = new CountDownLatch(1);
        CountDownLatch completeTestLatch = new CountDownLatch(2);

        ReadWriteLock lockFromInterceptor =
                (ReadWriteLock) ReflectionTestUtils.getField(interceptor, "lock");

        when(methodInvocationMock.getArguments()).thenReturn(new Object[] {"id", 123});

        when(uidSupplierMock.get()).thenReturn("http-request-1");

        Queue<Triple<String, Boolean, Boolean>> successfulCalls = new ConcurrentLinkedQueue<>();

        ThreadLocal<String> namedCallsThreadLocal = new ThreadLocal<>();

        when(cacheMock.get(eq("http-request-1"), isA(Callable.class))).thenAnswer(invocation -> {

            String operationName = namedCallsThreadLocal.get();

            successfulCalls.add(Triple.of(operationName, isReadLocked(lockFromInterceptor), isWriteLocked(lockFromInterceptor)));
            writeOperationLatch.countDown();
            readOperationLatch.await();

            return new HashMap<>();
        });

        @SuppressWarnings("unused")
        Future<Object> readOperationFuture = executorService.submit(() -> {
            try {
                namedCallsThreadLocal.set("Read Operation");
                Object result = interceptor.invoke(methodInvocationMock);
                completeTestLatch.countDown();
                return result;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        Future<Object> writeOperationFuture = executorService.submit(() -> {
            try {
                writeOperationLatch.await();
                namedCallsThreadLocal.set("Write Operation");
                Object result = interceptor.invoke(methodInvocationMock);
                completeTestLatch.countDown();
                return result;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        try {
            writeOperationFuture.get(BLOCKING_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            fail("This write operation should have timed out because it was blocked by the read lock");
        } catch (TimeoutException e) {
            // expected behaviour - the write operation was successfully blocked from writing to the cache.  Now allow
            // the readOperation to complete (which in turn allows the writeOperation to proceed) and wait on the
            // completeTestLatch until both the read and the write are fully finished.

            // sanity check that the read lock is indeed on and blocking progress at the moment
            assertTrue(isReadLocked(lockFromInterceptor));
            assertFalse(isWriteLocked(lockFromInterceptor));

            // assert that current progress has both operations having successfully read from the cache, but no writing
            // yet
            List<String> successfulOperationsSoFar = simpleMap(successfulCalls, Triple::getLeft);
            assertThat(successfulOperationsSoFar, contains("Read Operation", "Write Operation"));

            // now release the operations and await their successful completion
            readOperationLatch.countDown();
            completeTestLatch.await();
        }

        // assert that all operations were successful (both reads and both writes of the cache results)
        List<String> successfulOperationNames = simpleMap(successfulCalls, Triple::getLeft);
        assertThat(successfulOperationNames, hasSize(4));

        // we know the order of the first writes because it is "Read Operation" that initially acquires and holds the
        // read lock
        List<String> successfulInitialReads = successfulOperationNames.subList(0, 2);
        assertThat(successfulInitialReads, contains("Read Operation", "Write Operation"));

        // and we expect both operations to eventually write their results to the cache, but we can't guarantee the
        // order of this as they are both freed up at the same time by "Read Operation" unblocking
        List<String> successfulResultWrites = successfulOperationNames.subList(2, 4);
        assertThat(successfulResultWrites, containsInAnyOrder("Read Operation", "Write Operation"));

        // Assert that the read lock was on for both operations reading from the cache, but not on when the
        // operations then successfully wrote their results to the cache
        //
        // In turn, assert that the write lock was not on during the 2 operations' reads from the cache, but was on
        // during the operations' writing their results to the cache
        List<Boolean> successfulOperationReadLockStates = simpleMap(successfulCalls, Triple::getMiddle);
        List<Boolean> successfulOperationWriteLockStates = simpleMap(successfulCalls, Triple::getRight);
        assertThat(successfulOperationReadLockStates, contains(true, true, false, false));
        assertThat(successfulOperationWriteLockStates, contains(false, false, true, true));
    }

    /**
     * This test asserts that an operation holding the write lock in the {@link RestCacheMethodInterceptor#put} method
     * will block a second operation from being able to read its results from the cache.
     */
    @Test
    public void testWriteOperationBlocksReadOperations() throws Throwable {

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        CountDownLatch writeOperationLatch = new CountDownLatch(1);
        CountDownLatch readOperationLatch = new CountDownLatch(1);
        CountDownLatch completeTestLatch = new CountDownLatch(2);

        ReadWriteLock lockFromInterceptor =
                (ReadWriteLock) ReflectionTestUtils.getField(interceptor, "lock");

        when(methodInvocationMock.getArguments()).thenReturn(new Object[] {"id", 123});

        when(uidSupplierMock.get()).thenReturn("http-request-1");

        Queue<Triple<String, Boolean, Boolean>> successfulCalls = new ConcurrentLinkedQueue<>();

        ThreadLocal<String> namedCallsThreadLocal = new ThreadLocal<>();

        when(cacheMock.get(eq("http-request-1"), isA(Callable.class))).thenAnswer(invocation -> {

            String operationName = namedCallsThreadLocal.get();

            successfulCalls.add(Triple.of(operationName, isReadLocked(lockFromInterceptor), isWriteLocked(lockFromInterceptor)));

            // when "Write Operation" is writing its results to the cache, block and hold the write lock and allow
            // "Read Operation" to execute (and subsequently become blocked due to the write lock having been
            // aquired already
            if (isWriteLocked(lockFromInterceptor)) {
                readOperationLatch.countDown();
                writeOperationLatch.await();
            }

            return new HashMap<>();
        });

        @SuppressWarnings("unused")
        Future<Object> writeOperationFuture = executorService.submit(() -> {
            try {
                namedCallsThreadLocal.set("Write Operation");
                Object result = interceptor.invoke(methodInvocationMock);
                completeTestLatch.countDown();
                return result;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        Future<Object> readOperationFuture = executorService.submit(() -> {
            try {
                readOperationLatch.await();
                namedCallsThreadLocal.set("Read Operation");
                Object result = interceptor.invoke(methodInvocationMock);
                completeTestLatch.countDown();
                return result;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        try {
            readOperationFuture.get(BLOCKING_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            fail("This read operation should have timed out because it was blocked by the write lock");
        } catch (TimeoutException e) {
            // expected behaviour - the write operation was successfully blocked from writing to the cache.  Now allow
            // the writeOperation to complete (which in turn allows the readOperation to proceed) and wait on the
            // completeTestLatch until both writes are fully finished.

            // sanity check that the write lock is indeed on and blocking progress at the moment
            assertTrue(isWriteLocked(lockFromInterceptor));
            assertFalse(isReadLocked(lockFromInterceptor));

            // assert that current progress has "Write Operation" having successfully read from the cache and is now
            // in the process of writing to it (at which point it has blocked to keep the write lock aquired).
            //
            // The "Read Operation" is blocked because it is blocked from reading from the cache until "Write Operation"
            // has finished writing
            List<String> successfulOperationsSoFar = simpleMap(successfulCalls, Triple::getLeft);
            assertThat(successfulOperationsSoFar, contains("Write Operation", "Write Operation"));

            // now release the operations and await their successful completion
            writeOperationLatch.countDown();
            completeTestLatch.await();
        }

        // assert that all operations were successful (both reads and both writes of the cache results)
        List<String> successfulOperationNames = simpleMap(successfulCalls, Triple::getLeft);
        assertThat(successfulOperationNames, contains("Write Operation", "Write Operation", "Read Operation", "Read Operation"));

        // Assert that "Write Operation" successfully read its results and then stored its results, before then
        // letting "Read Operation" do the same
        List<Boolean> successfulOperationReadLockStates = simpleMap(successfulCalls, Triple::getMiddle);
        List<Boolean> successfulOperationWriteLockStates = simpleMap(successfulCalls, Triple::getRight);
        assertThat(successfulOperationReadLockStates, contains(true, false, true, false));
        assertThat(successfulOperationWriteLockStates, contains(false, true, false, true));
    }

    /**
     * This test asserts that an operation holding the write lock in the {@link RestCacheMethodInterceptor#put} method
     * will block a second operation from being able to write to the cache.
     */
    @Test
    public void testWriteOperationBlocksWriteOperations() throws Throwable {

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        CountDownLatch writeOperation1LatchWhenReadingFromCache = new CountDownLatch(1);
        CountDownLatch writeOperation1LatchWhenWritingToCache = new CountDownLatch(1);
        CountDownLatch writeOperation2LatchForInitialBlocking = new CountDownLatch(1);
        CountDownLatch completeTestLatch = new CountDownLatch(2);

        ReadWriteLock lockFromInterceptor =
                (ReadWriteLock) ReflectionTestUtils.getField(interceptor, "lock");

        when(methodInvocationMock.getArguments()).thenReturn(new Object[] {"id", 123});

        when(uidSupplierMock.get()).thenReturn("http-request-1");

        Queue<Triple<String, Boolean, Boolean>> successfulCalls = new ConcurrentLinkedQueue<>();

        ThreadLocal<String> namedCallsThreadLocal = new ThreadLocal<>();

        when(cacheMock.get(eq("http-request-1"), isA(Callable.class))).thenAnswer(invocation -> {

            String operationName = namedCallsThreadLocal.get();

            successfulCalls.add(Triple.of(operationName, isReadLocked(lockFromInterceptor), isWriteLocked(lockFromInterceptor)));

            // when "Write Operation 1" has successfully read its results from the cache, kick off "Write Operation 2" to
            // allow it to also read its results from the cache, and wait for it to successfully read from the cache
            if ("Write Operation 1".equals(operationName) && !isWriteLocked(lockFromInterceptor)) {
                writeOperation2LatchForInitialBlocking.countDown();
                writeOperation1LatchWhenReadingFromCache.await();
            }

            // when "Write Operation 2" has successfully read its results from the cache, unblock "Write Operation 1" to go
            // ahead and write its results to the cache (and block to aquire the write lock)
            if ("Write Operation 2".equals(operationName) && !isWriteLocked(lockFromInterceptor)) {
                writeOperation1LatchWhenReadingFromCache.countDown();
            }

            // We have ensured now that both Write Operations have had a chance to read their results from the cache.
            // Now they're both freed up to continue to write their results to the cache, and we can't guarantee which
            // one will get here first, but we can test that one will block the other.
            if (isWriteLocked(lockFromInterceptor)) {
                writeOperation1LatchWhenWritingToCache.await();
            }

            return new HashMap<>();
        });

        @SuppressWarnings("unused")
        Future<Object> writeOperation1Future = executorService.submit(() -> {
            try {
                namedCallsThreadLocal.set("Write Operation 1");
                Object result = interceptor.invoke(methodInvocationMock);
                completeTestLatch.countDown();
                return result;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        Future<Object> writeOperation2Future = executorService.submit(() -> {
            try {
                writeOperation2LatchForInitialBlocking.await();
                namedCallsThreadLocal.set("Write Operation 2");
                Object result = interceptor.invoke(methodInvocationMock);
                completeTestLatch.countDown();
                return result;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        try {
            writeOperation1Future.get(BLOCKING_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            writeOperation2Future.get(BLOCKING_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            fail("One of these write operations should have timed out because it was blocked by the write lock " +
                    "aquired by the other");
        } catch (TimeoutException e) {

            // expected behaviour - one of the write operations was successfully blocked from writing to the cache.
            // Now allow the blocking write operation to complete (which in turn allows the other operation to proceed)
            // and wait on the completeTestLatch until both writes are fully finished.

            // sanity check that the write lock is indeed on and blocking progress at the moment
            assertTrue(isWriteLocked(lockFromInterceptor));
            assertFalse(isReadLocked(lockFromInterceptor));

            // assert that current progress has both operations having successfully read from the cache and one of the
            // operations is currently in the process of writing its result to the cache
            List<String> successfulOperationsSoFar = simpleMap(successfulCalls, Triple::getLeft);
            assertThat(successfulOperationsSoFar, hasSize(3));

            // the first 2 entries are the 2 successful reads.  The 3rd entry is whichever write operation successfully
            // started to write to the cache first
            List<String> successfulReadOperationsSoFar = successfulOperationsSoFar.subList(0, 2);
            assertThat(successfulReadOperationsSoFar, contains("Write Operation 1", "Write Operation 2"));

            // now release the operations and await their successful completion
            writeOperation1LatchWhenWritingToCache.countDown();
            completeTestLatch.await();
        }

        // assert that all operations were successful (both reads and both writes of the cache results)
        List<String> successfulOperationNames = simpleMap(successfulCalls, Triple::getLeft);
        List<String> successfulReadOperations = successfulOperationNames.subList(0, 2);
        assertThat(successfulReadOperations, contains("Write Operation 1", "Write Operation 2"));
        List<String> successfulWriteOperations = successfulOperationNames.subList(2, 4);
        assertThat(successfulWriteOperations, containsInAnyOrder("Write Operation 1", "Write Operation 2"));

        // Assert that "Write Operation 1" successfully read its results, then "Write Operation 2" read its results,
        // and then finally they both wrote their results
        List<Boolean> successfulOperationReadLockStates = simpleMap(successfulCalls, Triple::getMiddle);
        List<Boolean> successfulOperationWriteLockStates = simpleMap(successfulCalls, Triple::getRight);
        assertThat(successfulOperationReadLockStates, contains(true, true, false, false));
        assertThat(successfulOperationWriteLockStates, contains(false, false, true, true));
    }
}
