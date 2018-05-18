package org.innovateuk.ifs.async.controller;

import org.innovateuk.ifs.async.ReadWriteLockTestHelper;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.async.AsyncExecutionTestHelper.BLOCKING_TIMEOUT_MILLIS;
import static org.innovateuk.ifs.async.ReadWriteLockTestHelper.isReadLocked;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test for the thread safety of a {@link org.springframework.ui.Model} wrapped in a {@link ThreadsafeModel} wrapper.
 */
public class ThreadsafeModelTest {

    @Test
    public void testCallsToContainsAttributeDoNotLockOtherReadOperations() throws ExecutionException, InterruptedException {

        assertFirstReadOperationDoesNotBlockSecondReadOperation(
                model -> model.containsAttribute("read1"),
                model -> model.containsAttribute("read2"));

        assertFirstReadOperationDoesNotBlockSecondReadOperation(
                model -> model.containsAttribute("read"), Model::asMap);
    }

    @Test
    public void testCallsToAsMapDoNotLockOtherReadOperations() throws ExecutionException, InterruptedException {

        assertFirstReadOperationDoesNotBlockSecondReadOperation(
                Model::asMap, model -> model.containsAttribute("read"));
    }

    @Test
    public void testCallsToContainsAttributeLocksWriteAccessUntilComplete() throws ExecutionException, InterruptedException {

        assertFirstOperationBlocksSecondOperationUntilComplete(
                model -> model.containsAttribute("read"),
                model -> model.addAttribute("write"),
                ReadWriteLockTestHelper::isReadLocked,
                ReadWriteLockTestHelper::isWriteLocked);
    }

    @Test
    public void testCallsToAsMapLocksWriteAccessUntilComplete() throws ExecutionException, InterruptedException {

        assertFirstOperationBlocksSecondOperationUntilComplete(
                Model::asMap,
                model -> model.addAllAttributes(Collections.singleton("write")),
                ReadWriteLockTestHelper::isReadLocked,
                ReadWriteLockTestHelper::isWriteLocked);

        assertFirstOperationBlocksSecondOperationUntilComplete(
                Model::asMap,
                model -> model.mergeAttributes(asMap(1, 2)),
                ReadWriteLockTestHelper::isReadLocked,
                ReadWriteLockTestHelper::isWriteLocked);
    }

    @Test
    public void testCallsToAddAttributeLocksReadAccessUntilComplete() throws ExecutionException, InterruptedException {

        assertFirstOperationBlocksSecondOperationUntilComplete(
                model -> model.addAttribute("write"),
                model -> model.containsAttribute("read"),
                ReadWriteLockTestHelper::isWriteLocked,
                ReadWriteLockTestHelper::isReadLocked);
    }

    @Test
    public void testCallsToAddAllAttributesLocksReadAccessUntilComplete() throws ExecutionException, InterruptedException {

        assertFirstOperationBlocksSecondOperationUntilComplete(
                model -> model.addAllAttributes(singletonList("write")),
                Model::asMap,
                ReadWriteLockTestHelper::isWriteLocked,
                ReadWriteLockTestHelper::isReadLocked);
    }

    @Test
    public void testCallsToMergeAttributesLocksReadAccessUntilComplete() throws ExecutionException, InterruptedException {

        assertFirstOperationBlocksSecondOperationUntilComplete(
                model -> model.mergeAttributes(asMap(1, 2)),
                Model::asMap,
                ReadWriteLockTestHelper::isWriteLocked,
                ReadWriteLockTestHelper::isReadLocked);
    }

    @Test
    public void testCallsToAddAttributeLocksWriteAccessUntilComplete() throws ExecutionException, InterruptedException {

        assertFirstOperationBlocksSecondOperationUntilComplete(
                model -> model.addAttribute("write1"),
                model -> model.addAttribute("write2"),
                ReadWriteLockTestHelper::isWriteLocked,
                ReadWriteLockTestHelper::isWriteLocked);
    }

    @Test
    public void testCallsToAddAllAttributesLocksWriteAccessUntilComplete() throws ExecutionException, InterruptedException {

        assertFirstOperationBlocksSecondOperationUntilComplete(
                model -> model.addAllAttributes(singletonList("write")),
                model -> model.mergeAttributes(asMap(1, 2)),
                ReadWriteLockTestHelper::isWriteLocked,
                ReadWriteLockTestHelper::isWriteLocked);
    }

    @Test
    public void testCallsToMergeAttributesLocksWriteAccessUntilComplete() throws ExecutionException, InterruptedException {

        assertFirstOperationBlocksSecondOperationUntilComplete(
                model -> model.mergeAttributes(asMap(1, 2)),
                model -> model.addAllAttributes(singletonList("write")),
                ReadWriteLockTestHelper::isWriteLocked,
                ReadWriteLockTestHelper::isWriteLocked);
    }

    /**
     * A non-standard thing to test, but as the mechanics of this test are quite tricky to implement, we want to
     * ensure that it is not providing false-positives
     */
    @Test
    public void testThatTheTestMechanismWorks() throws ExecutionException, InterruptedException {

        try {
            assertFirstOperationBlocksSecondOperationUntilComplete(
                    model -> model.containsAttribute("read1"),
                    model -> model.containsAttribute("read2"),
                    ReadWriteLockTestHelper::isReadLocked,
                    ReadWriteLockTestHelper::isReadLocked);

            fail("2 read operations should not block each other and therefore there must be a problem with the test mechanism");

        } catch (AssertionError e) {

            // expected behaviour
        }
    }

    /**
     * This test tests that we can have as many concurrent reads as we like whilst maintaining thread safety.
     *
     * This test issues 2 calls to {@link ThreadsafeModel#containsAttribute(String)}, the first of which blocks *within*
     * the bounds of the threadsafe locking mechanism inside ThreadsafeModel until the 2nd call is executed, upon which
     * the first call is unblocked.  The test performs this sequence in such a way so as to allow the 2nd call to write
     * its value to the "operationValues" list before the 1st call does, and we can assert that the 2nd value always makes it
     * into the list before the first does.
     *
     * This test proves therefore that a 2nd "read" operation can occur whilst a first "read" operation is in progress
     * (simulated by blocking the 1st call on the countDownLatch until the 2nd one is ready to release it).
     */
    private void assertFirstReadOperationDoesNotBlockSecondReadOperation(Consumer<Model> read1Operation, Consumer<Model> read2Operation) throws InterruptedException, ExecutionException {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        Model wrappedModel = mock(Model.class);

        ThreadsafeModel threadsafeModel = new ThreadsafeModel(wrappedModel);

        ReadWriteLock lockFromModel = (ReadWriteLock) ReflectionTestUtils.getField(threadsafeModel, "lock");

        List<String> operationValues = new ArrayList<>();

        Model read1WhenAnswer = doAnswer(invocation -> {
            assertTrue(isReadLocked(lockFromModel));
            countDownLatch.await();
            operationValues.add("read1 operation");
            return null;
        }).when(wrappedModel);

        read1Operation.accept(read1WhenAnswer);

        Model read2WhenAnswer = doAnswer(invocation -> {
            assertTrue(isReadLocked(lockFromModel));
            operationValues.add("read2 operation");
            countDownLatch.countDown();
            return null;
        }).when(wrappedModel);

        read2Operation.accept(read2WhenAnswer);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<?> read1Future = executor.submit(() -> read1Operation.accept(threadsafeModel));
        Future<?> read2Future = executor.submit(() -> read2Operation.accept(threadsafeModel));

        read1Future.get();
        read2Future.get();

        Model read1Verification = verify(wrappedModel, atLeastOnce());
        read1Operation.accept(read1Verification);

        Model read2Verification = verify(wrappedModel, atLeastOnce());
        read2Operation.accept(read2Verification);

        assertEquals("read2 operation", operationValues.get(0));
        assertEquals("read1 operation", operationValues.get(1));
    }

    /**
     * This test tests that a locking operation will block access to the wrapped {@link Model} methods until operation1 has finished.
     *
     * The test initiates 2 mutually exclusive operations at the same time, but ensures that the operation1 will
     * make it into the locking boundary of {@link ThreadsafeModel} before operation2 is allowed to properly begin (by
     * use of the operation2CountDownLatch).
     *
     * The operation1 will then wait 50ms before writing its value to the "operationValues" list.  This should be the first
     * item of the list every time because operation2 cannot write its own value to the list until operation2 is
     * complete.  Therefore it has been successfully blocked for the 50ms wait time where operation1 had control of
     * the lock.
     */
    private void assertFirstOperationBlocksSecondOperationUntilComplete(
            Consumer<Model> operation1, Consumer<Model> operation2,
            Function<ReadWriteLock, Boolean> expectedLockStateDuringOperation1Test,
            Function<ReadWriteLock, Boolean> expectedLockStateDuringOperation2Test)
            throws InterruptedException, ExecutionException {

        CountDownLatch operation1CountDownLatch = new CountDownLatch(1);
        CountDownLatch operation2CountDownLatch = new CountDownLatch(1);

        Model wrappedModel = mock(Model.class);

        ThreadsafeModel threadsafeModel = new ThreadsafeModel(wrappedModel);

        ReadWriteLock lockFromModel = (ReadWriteLock) ReflectionTestUtils.getField(threadsafeModel, "lock");

        List<String> operationValues = new ArrayList<>();

        Model operation1Answer = doAnswer(invocation -> {
            assertTrue(expectedLockStateDuringOperation1Test.apply(lockFromModel));
            operation2CountDownLatch.countDown();
            operation1CountDownLatch.await(BLOCKING_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            operationValues.add("operation 1");
            return null;
        }).when(wrappedModel);

        operation1.accept(operation1Answer);

        Model operation2Answer = doAnswer(invocation -> {
            assertTrue(expectedLockStateDuringOperation2Test.apply(lockFromModel));
            operationValues.add("operation 2");
            return null;
        }).when(wrappedModel);

        operation2.accept(operation2Answer);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<?> operation1Future = executor.submit(() -> operation1.accept(threadsafeModel));

        Future<?> operation2Future = executor.submit(() -> {
            operation2CountDownLatch.await();
            operation2.accept(threadsafeModel);
            return null;
        });

        operation1Future.get();
        operation2Future.get();

        Model operation1Verification = verify(wrappedModel);
        operation1.accept(operation1Verification);

        Model operation2Verification = verify(wrappedModel);
        operation2.accept(operation2Verification);

        assertEquals("operation 1", operationValues.get(0));
        assertEquals("operation 2", operationValues.get(1));
    }
}
