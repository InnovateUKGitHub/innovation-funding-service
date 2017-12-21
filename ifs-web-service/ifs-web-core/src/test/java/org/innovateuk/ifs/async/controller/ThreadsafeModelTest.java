package org.innovateuk.ifs.async.controller;

import org.junit.Test;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
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
                model -> model.containsAttribute("read1"), Model::asMap);
    }

    @Test
    public void testCallsToAsMapDoNotLockOtherReadOperations() throws ExecutionException, InterruptedException {

        assertFirstReadOperationDoesNotBlockSecondReadOperation(
                Model::asMap, model -> model.containsAttribute("read2"));
    }

    /**
     * This test tests that we can have as many concurrent reads as we like whilst maintaing thread safety.
     *
     * This test issues 2 calls to {@link ThreadsafeModel#containsAttribute(String)}, the first of which blocks *within*
     * the bounds of the threadsafe locking mechanism inside ThreadsafeModel until the 2nd call is executed, upon which
     * the first call is unblocked.  The test performs this sequence in such a way so as to allow the 2nd call to write
     * its value to the "readValues" list before the 1st call does, and we can assert that the 2nd value always makes it
     * into the list before the first does.
     *
     * This test proves therefore that a 2nd "read" operation can occur whilst a first "read" operation is in progress
     * (simulated by blocking the 1st call on the countDownLatch until the 2nd one is ready to release it).
     */
    private void assertFirstReadOperationDoesNotBlockSecondReadOperation(Consumer<Model> read1Operation, Consumer<Model> read2Operation) throws InterruptedException, ExecutionException {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        Model wrappedModel = mock(Model.class);

        ThreadsafeModel threadsafeModel = new ThreadsafeModel(wrappedModel);

        List<String> readValues = new ArrayList<>();

        Model read1WhenAnswer = doAnswer(invocation -> {
            blockUntilRead2IsSuccessful(countDownLatch);
            readValues.add("read1 answer");
            return null;
        }).when(wrappedModel);

        read1Operation.accept(read1WhenAnswer);

        Model read2WhenAnswer = doAnswer(invocation -> {
            readValues.add("read2 answer");
            unblockRead1(countDownLatch);
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

        assertEquals("read2 answer", readValues.get(0));
        assertEquals("read1 answer", readValues.get(1));
    }

    private void blockUntilRead2IsSuccessful(CountDownLatch countDownLatch) throws InterruptedException {
        countDownLatch.await();
    }

    private void unblockRead1(CountDownLatch countDownLatch) {
        countDownLatch.countDown();
    }
}
