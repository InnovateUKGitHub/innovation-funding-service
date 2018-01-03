package org.innovateuk.ifs.async.generation;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * Tests for the {@link AsyncFuturesGenerator#awaitAll} methods.  These methods allow us to await the completion of other
 * Futures before performing another Future piece of work.  Above and beyond {@link CompletableFuture#allOf(CompletableFuture[])},
 * these methods also register the newly generated Future with {@link AsyncFuturesHolder} so that we can await its completion
 * during the execution of Controller methods (to prevent Thymeleaf form attempting to render pages before all Futures
 * have completed writing data to the model.
 */
public class AsyncFuturesGeneratorAwaitAllIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AsyncFuturesGenerator generator;

    @Before
    public void clearRegisteredFutures() {
        AsyncFuturesHolder.clearFutures();
    }

    /**
     * This is a simple test for the {@link AsyncFuturesGenerator#awaitAll(CompletableFuture)} method that checks that
     * the awaiting Future successfully waits for the first Future to complete and then uses its value to perform some
     * new work.
     */
    @Test
    public void testSimpleAwaitAll() throws ExecutionException, InterruptedException {

        CompletableFuture<Integer> future = generator.async(() -> 3);
        CompletableFuture<Integer> awaitingFuture = generator.awaitAll(future).thenApply(result -> result * 3);

        Integer finalResult = awaitingFuture.get();
        assertEquals(Integer.valueOf(9), finalResult);
    }

    /**
     * This tests that futures generated via {@link AsyncFuturesGenerator#awaitAll(CompletableFuture)} are registered with
     * {@link AsyncFuturesHolder} so that we can block on them when completing Controller methods.
     */
    @Test
    public void testSimpleAwaitAllRegistersNewAwaitingFuture() throws ExecutionException, InterruptedException {

        CompletableFuture<Integer> future = generator.async(() -> 3);
        CompletableFuture<Integer> awaitingFuture = generator.awaitAll(future).thenApply(result -> result * 3);

        List<RegisteredAsyncFutureDetails> registeredFutures = new ArrayList<>(AsyncFuturesHolder.getFuturesOrInitialise());
            assertEquals(2, registeredFutures.size());
        assertEquals(future, registeredFutures.get(0).getFuture());
        assertEquals(awaitingFuture, registeredFutures.get(1).getFuture());
    }

    /**
     * This test is for {@link AsyncFuturesGenerator#awaitAll(CompletableFuture, CompletableFuture)} to show that 2
     * futures can be awaited on and that their subsequent results will be provided as a tuple-2 to the new Future
     * to perform some new work on the multiple results of the dependent Futures.
     */
    @Test
    public void testTuple2AwaitAllFuture() throws ExecutionException, InterruptedException {

        CompletableFuture<Integer> future1 = generator.async(() -> 3);
        CompletableFuture<Integer> future2 = generator.async(() -> 4);
        CompletableFuture<Integer> awaitingFuture = generator.awaitAll(future1, future2).thenApply((r1, r2) -> r1 + r2);

        Integer finalResult = awaitingFuture.get();
        assertEquals(Integer.valueOf(7), finalResult);

        List<RegisteredAsyncFutureDetails> registeredFutures = new ArrayList<>(AsyncFuturesHolder.getFuturesOrInitialise());
        assertEquals(3, registeredFutures.size());
        assertEquals(future1, registeredFutures.get(0).getFuture());
        assertEquals(future2, registeredFutures.get(1).getFuture());
        assertEquals(awaitingFuture, registeredFutures.get(2).getFuture());
    }

    /**
     * This test is for {@link AsyncFuturesGenerator#awaitAll(CompletableFuture, CompletableFuture, CompletableFuture)}
     * to show that 3 futures can be awaited on and that their subsequent results will be provided as a tuple-3 to the
     * new Future to perform some new work on the multiple results of the dependent Futures.
     */
    @Test
    public void testTuple3AwaitAllFuture() throws ExecutionException, InterruptedException {

        CompletableFuture<Integer> future1 = generator.async(() -> 3);
        CompletableFuture<Integer> future2 = generator.async(() -> 4);
        CompletableFuture<Integer> future3 = generator.async(() -> 5);
        CompletableFuture<Integer> awaitingFuture = generator.awaitAll(future1, future2, future3).thenApply((r1, r2, r3) -> r1 + r2 + r3);

        Integer finalResult = awaitingFuture.get();
        assertEquals(Integer.valueOf(12), finalResult);

        List<RegisteredAsyncFutureDetails> registeredFutures = new ArrayList<>(AsyncFuturesHolder.getFuturesOrInitialise());
        assertEquals(4, registeredFutures.size());
        assertEquals(future1, registeredFutures.get(0).getFuture());
        assertEquals(future2, registeredFutures.get(1).getFuture());
        assertEquals(future3, registeredFutures.get(2).getFuture());
        assertEquals(awaitingFuture, registeredFutures.get(3).getFuture());
    }

    /**
     * This test is for {@link AsyncFuturesGenerator#awaitAll(CompletableFuture, CompletableFuture, CompletableFuture, CompletableFuture[])}
     * to show that more than 3 futures can be awaited on.
     */
    @Test
    public void testTupleNAwaitAllFuture() throws ExecutionException, InterruptedException {

        CompletableFuture<Integer> future1 = generator.async(() -> 3);
        CompletableFuture<Integer> future2 = generator.async(() -> 4);
        CompletableFuture<Integer> future3 = generator.async(() -> 5);
        CompletableFuture<Integer> future4 = generator.async(() -> 6);
        CompletableFuture<Integer> future5 = generator.async(() -> 7);

        List<Integer> results = new ArrayList<>();

        CompletableFuture<Void> awaitingFuture =
                generator.awaitAll(future1, future2, future3, future4, future5).thenApply(() -> {
                    results.addAll(asList(future1.get()));
                    results.addAll(asList(future2.get()));
                    results.addAll(asList(future3.get()));
                    results.addAll(asList(future4.get()));
                    results.addAll(asList(future5.get()));
                    return null;
                });

        awaitingFuture.get();
        assertEquals(asList(3, 4, 5, 6, 7), results);

        List<RegisteredAsyncFutureDetails> registeredFutures = new ArrayList<>(AsyncFuturesHolder.getFuturesOrInitialise());
        assertEquals(6, registeredFutures.size());
        assertEquals(future1, registeredFutures.get(0).getFuture());
        assertEquals(future2, registeredFutures.get(1).getFuture());
        assertEquals(future3, registeredFutures.get(2).getFuture());
        assertEquals(future4, registeredFutures.get(3).getFuture());
        assertEquals(future5, registeredFutures.get(4).getFuture());
        assertEquals(awaitingFuture, registeredFutures.get(5).getFuture());
    }
}
