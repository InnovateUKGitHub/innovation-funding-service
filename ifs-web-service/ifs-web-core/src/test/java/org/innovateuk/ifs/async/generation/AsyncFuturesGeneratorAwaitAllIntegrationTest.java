package org.innovateuk.ifs.async.generation;

import org.innovateuk.ifs.async.controller.AsyncAllowedThreadLocal;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import static java.lang.Thread.currentThread;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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

    @Before
    public void enableAsync() {
        AsyncAllowedThreadLocal.setAsyncAllowed(true);
    }

    @After
    public void disableAsync() {
        AsyncAllowedThreadLocal.clearAsyncAllowed();
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

        List<RegisteredAsyncFutureDetails<?>> registeredFutures = new ArrayList<>(AsyncFuturesHolder.getFuturesOrInitialise());
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

        List<RegisteredAsyncFutureDetails<?>> registeredFutures = new ArrayList<>(AsyncFuturesHolder.getFuturesOrInitialise());
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

        List<RegisteredAsyncFutureDetails<?>> registeredFutures = new ArrayList<>(AsyncFuturesHolder.getFuturesOrInitialise());
        assertEquals(4, registeredFutures.size());
        assertEquals(future1, registeredFutures.get(0).getFuture());
        assertEquals(future2, registeredFutures.get(1).getFuture());
        assertEquals(future3, registeredFutures.get(2).getFuture());
        assertEquals(awaitingFuture, registeredFutures.get(3).getFuture());
    }

    /**
     * This test is for {@link AsyncFuturesGenerator#awaitAll(CompletableFuture, CompletableFuture, CompletableFuture, CompletableFuture)}
     * to show that 4 futures can be awaited on and that their subsequent results will be provided as a tuple-4 to the
     * new Future to perform some new work on the multiple results of the dependent Futures.
     */
    @Test
    public void testTuple4AwaitAllFuture() throws ExecutionException, InterruptedException {

        CompletableFuture<Integer> future1 = generator.async(() -> 3);
        CompletableFuture<Integer> future2 = generator.async(() -> 4);
        CompletableFuture<Integer> future3 = generator.async(() -> 5);
        CompletableFuture<Integer> future4 = generator.async(() -> 6);
        CompletableFuture<Integer> awaitingFuture = generator.awaitAll(future1, future2, future3, future4).thenApply((r1, r2, r3, r4) -> r1 + r2 + r3 + r4);

        Integer finalResult = awaitingFuture.get();
        assertEquals(Integer.valueOf(18), finalResult);

        List<RegisteredAsyncFutureDetails<?>> registeredFutures = new ArrayList<>(AsyncFuturesHolder.getFuturesOrInitialise());
        assertEquals(5, registeredFutures.size());
        assertEquals(future1, registeredFutures.get(0).getFuture());
        assertEquals(future2, registeredFutures.get(1).getFuture());
        assertEquals(future3, registeredFutures.get(2).getFuture());
        assertEquals(future4, registeredFutures.get(3).getFuture());
        assertEquals(awaitingFuture, registeredFutures.get(4).getFuture());
    }

    /**
     * This test is for {@link AsyncFuturesGenerator#awaitAll(CompletableFuture, CompletableFuture, CompletableFuture, CompletableFuture, CompletableFuture[])}
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
                generator.awaitAll(future1, future2, future3, future4, future5).thenApply(futureResults -> {
                    List<Integer> futureResultIntegers = (List<Integer>) futureResults;
                    results.addAll(singletonList(futureResultIntegers.get(0)));
                    results.addAll(singletonList(futureResultIntegers.get(1)));
                    results.addAll(singletonList(futureResultIntegers.get(2)));
                    results.addAll(singletonList(futureResultIntegers.get(3)));
                    results.addAll(singletonList(futureResultIntegers.get(4)));
                    return null;
                });

        awaitingFuture.get();
        assertEquals(asList(3, 4, 5, 6, 7), results);

        List<RegisteredAsyncFutureDetails<?>> registeredFutures = new ArrayList<>(AsyncFuturesHolder.getFuturesOrInitialise());
        assertEquals(6, registeredFutures.size());
        assertEquals(future1, registeredFutures.get(0).getFuture());
        assertEquals(future2, registeredFutures.get(1).getFuture());
        assertEquals(future3, registeredFutures.get(2).getFuture());
        assertEquals(future4, registeredFutures.get(3).getFuture());
        assertEquals(future5, registeredFutures.get(4).getFuture());
        assertEquals(awaitingFuture, registeredFutures.get(5).getFuture());
    }

    /**
     * Test that the Futures created via {@link AsyncFuturesGenerator#awaitAll} methods are executed via an executor
     * thread, and that it has access to the ThreadLocals that other executor Threads have access to.
     */
    @Test
    public void testAwaitingFutureExecutesInExecutorThread() throws ExecutionException, InterruptedException {

        Authentication authentication = new UserAuthentication(newUserResource().build());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CompletableFuture<Thread> future1 = generator.async(() -> {
            assertThat(SecurityContextHolder.getContext().getAuthentication(), sameInstance(authentication));
            return currentThread();
        });

        CompletableFuture<Thread> future2 = generator.async(() -> {
            assertThat(SecurityContextHolder.getContext().getAuthentication(), sameInstance(authentication));
            return currentThread();
        });

        CompletableFuture<Thread> awaitingFuture = generator.awaitAll(future1, future2).thenApply((r1, r2) -> {
            assertThat(SecurityContextHolder.getContext().getAuthentication(), sameInstance(authentication));
            return currentThread();
        });

        // check that the awaitAll() future was executed by an executor thread
        Thread awaitingFutureThread = awaitingFuture.get();
        assertThat(awaitingFutureThread.getName(), startsWith("IFS-Async-Executor-"));
    }

    /**
     * This test asserts that {@link AsyncFuturesGenerator#awaitAll} methods wait for not only their directly dependent
     * futures to complete, but also for their dependents' descendants too.
     *
     * This test uses a mix of descendant Futures generated via {@link AsyncFuturesGenerator#async} and
     * {@link AsyncFuturesGenerator#awaitAll} to test that awaitingFuture will wait for the completion of all of them
     * before
     */
    @Test
    public void testAwaitAllFutureWaitsForCompletionOfAllDependentsDescendantFuturesBeforeContinuing() throws ExecutionException, InterruptedException {

        List<String> completedFutures = new ArrayList<>();

        // future1 creates a child Future which in turn creates 2 child Futures of its own
        CompletableFuture<Void> future1 = generator.async(() -> {

            generator.async(() -> {

                CompletableFuture<Void> future1ChildChild = generator.async(() -> {
                    Thread.sleep(40);
                    completedFutures.add("future1ChildChild");
                });

                generator.awaitAll(future1ChildChild).thenAccept(f1 ->
                        completedFutures.add("future1ChildChildAwaiting"));
            });
        });

        // future2 creates a child Future which in turn creates another child Future of its own
        CompletableFuture<Void> future2 = generator.async(() -> {

            generator.async(() ->

                generator.async(() -> {
                    Thread.sleep(10);
                    completedFutures.add("future2ChildChild");
                })
            );
        });

        CountDownLatch unrelatedFutureLatch = new CountDownLatch(1);

        // this is an unrelated Future that awaitingFuture should not be dependent on.  It immediately locks and stays
        // locked until awaitingFuture executes, proving that awaitingFuture doesn't have to wait for it to complete
        // before itself executing
        generator.async(() -> unrelatedFutureLatch.await());

        // assert that the future1ChildChild Future, future2ChildChild and future1ChildChildAwaiting have all completed
        // prior to this Future executing.  The delays in completing these other Futures show that this one waits for
        // their completion.  The fact that this Future executes also shows that it awaited only on specific Futures
        // rather than all in-flight Futures, as the "unrelatedFuture" which is sat awaiting its latch to be unlocked
        // has of course not completed by the time this Future executes (as this Future unlocks it)
        CompletableFuture<List<String>> awaitingFuture = generator.awaitAll(future1, future2).thenApply((r1, r2) -> {
            List<String> futuresCompletedAtThisPoint = new ArrayList<>(completedFutures);
            unrelatedFutureLatch.countDown();
            return futuresCompletedAtThisPoint;
        });

        // assert that the correct Futures have completed before awaitingFuture was executed.
        List<String> completedFuturesWhenAwaitingFuturesRan = awaitingFuture.get();
        assertThat(completedFuturesWhenAwaitingFuturesRan, contains("future2ChildChild","future1ChildChild", "future1ChildChildAwaiting"));
    }

    /**
     * This method tests that awaitAlls can be given explicit Future names as well as async()-created Futures, which is
     * useful for debugging purposes.
     */
    @Test
    public void testNamedAwaitAlls() throws ExecutionException, InterruptedException {

        CompletableFuture<Void> future = generator.async("future", () -> {});

        CompletableFuture<Void> awaitAll = generator.awaitAll("Waiting for future", future).thenAccept(f1 -> {

            CompletableFuture<Void> childFuture = generator.async("childFuture", () -> {});

            generator.awaitAll("Waiting for childFuture", childFuture).thenApply(f2 -> null);
        });

        awaitAll.get();

        List<RegisteredAsyncFutureDetails<?>> registeredFutures = new ArrayList<>(AsyncFuturesHolder.getFuturesOrInitialise());
        List<String> futureNames = simpleMap(registeredFutures, RegisteredAsyncFutureDetails::getFutureName);
        assertThat(futureNames, contains("future", "Waiting for future", "childFuture", "Waiting for childFuture"));
    }
}
