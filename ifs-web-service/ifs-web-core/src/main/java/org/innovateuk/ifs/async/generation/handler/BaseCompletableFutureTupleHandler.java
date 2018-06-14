package org.innovateuk.ifs.async.generation.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.async.exceptions.AsyncException;
import org.innovateuk.ifs.async.generation.AsyncFuturesHolder;
import org.innovateuk.ifs.async.util.AsyncAllowedThreadLocal;
import org.innovateuk.ifs.util.ExceptionThrowingSupplier;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Base class for convenient handling of multiple CompletableFutures.
 *
 * The main benefit that these classes give is to allow the developer handle futures coding with a little nicer syntax.
 * For instance, rather than writing:
 *
 * <pre>
 * {@code
 *
 * CompletableFuture<Void> combination = CompletableFuture.allOf(future1, future2);
 *
 * combination.thenApply(() -> {
 *
 *    F1 future1Result = future1.get();
 *    F2 future2Result = future2.get();
 *
 *    return doSomethingWith(future1Result, future2Result);
 * });
 *
 * }
 * </pre>
 *
 * the developer can instead write:
 *
 * <pre>
 * {@code
 *
 * awaitAll(future1, future2).thenApply((future1Result, future2Result) ->
 *      doSomethingWith(future1Result, future2Result));
 *
 * }
 * </pre>
 *
 * which is much more concise.
 *
 * The secondary benefit is that generating a CompletableFuture using these classes' thenAccept() or thenApply() methods
 * registers the newly generated CompletableFuture with AsyncFuturesHolder for the developer (which in turn prevents the
 * Controller from attempting to render any template until the registered CompletableFuture has fully completed).
 *
 * The third benefit is that generating a CompletableFuture using these classes' thenAccept() or thenApply() methods
 * will ensure that any descendant Futures spawned from these Futures and their children will be fully completed prior
 * to the next chained Future being executed, leading to more consistent behaviour (at the slight cost of slightly less
 * available parallelisation)
 */
abstract class BaseCompletableFutureTupleHandler {

    private static final Log LOG = LogFactory.getLog(BaseCompletableFutureTupleHandler.class);

    private String futureName;
    private Executor threadPool;
    private CompletableFuture<?>[] futures;

    BaseCompletableFutureTupleHandler(String futureName, Executor threadPool, CompletableFuture<?>... futures) {
        this.futureName = futureName;
        this.threadPool = threadPool;
        this.futures = futures;
    }

    /**
     * This method is the main implementation of awaitAll(future1, future2).thenApply((f1, f2) -> {... child future});
     *
     * In addition to creating "child future" and chaining it to execute after future1 and future2 have completed
     * (essentially out-of-the-box CompletableFuture.allOf(...).thenApply() functionality), this method additionally
     * enforces consistency in the order of execution of these futures above and beyond the out-of-the-box
     * CompletableFuture functionality.
     *
     * Firstly, rather than just waiting for future1 and future2 to complete prior to executing "child future", this
     * method will guarantee that any child futures created by future1 and future2 have also completed, and any further
     * descendants too.
     *
     * Secondly, this method registers "child future" with {@link AsyncFuturesHolder} so that we can wait on its
     * completion prior to letting Controller methods complete.  "child future" will always be registered before any of
     * its potential child futures are registered because its execution is deferred until it is fully registered, thus
     * giving us additional consistency in the order that things are registered and executed.
     */
    <R> CompletableFuture<R> thenApplyInternal(ExceptionThrowingSupplier<R> supplier) {

        Supplier<R> waitingSupplier = () -> {

            // this ensures that all of the top-level Futures' descendant Futures are fully completed prior to
            // executing the next Future
            waitForFuturesAndDescendantsToFullyComplete();
            try {
                return supplier.get();
            } catch (Exception e) {
                LOG.error("Error whilst executing Supplier Future", e);
                throw AsyncException.getOriginalAsyncExceptionOrWrapInAsyncException(e, () -> "Error whilst executing Supplier Future - wrapping in AsyncException");
            }
        };

        CompletableFuture<Void> joiningFuture = CompletableFuture.allOf(futures);

        if (AsyncAllowedThreadLocal.isAsyncAllowed()) {

            // Ensure that this future is registered prior to it executing and potentially registering more futures.
            // This gives consistency to the order of future registration - this future will register first, and then
            // any futures that this future might create will be guaranteed to register afterwards always
            CountDownLatch waitForRegistrationLatch = new CountDownLatch(1);

            CompletableFuture<R> blockingFuture = joiningFuture.thenApplyAsync(done -> {
                waitForRegistration(waitForRegistrationLatch);
                return waitingSupplier.get();
            }, threadPool);

            // register this future and then allow it to execute
            CompletableFuture<R> registeredFuture = AsyncFuturesHolder.registerFuture(futureName, blockingFuture);
            waitForRegistrationLatch.countDown();
            return registeredFuture;

        } else {

            return joiningFuture.thenApply(done -> waitingSupplier.get());
        }
    }

    void waitForFuturesAndDescendantsToFullyComplete() {

        // this ensures that all of the top-level Futures and their descendant Futures are fully completed prior to
        // executing the next Future
        AsyncFuturesHolder.waitForFuturesAndChildFuturesToCompleteFrom(asList(futures));
    }

    <R> R getResult(int index)  {
        return getResult(futures[index]);
    }

    List<?> getResultsAsList()  {
        return simpleMap(futures, this::getResult);
    }

    private <R> R getResult(CompletableFuture<?> future)  {
        try {
            return (R) future.get();
        } catch (Exception e) {
            LOG.error("Error whilst attempting to get future result", e);
            throw AsyncException.getOriginalAsyncExceptionOrWrapInAsyncException(e, () -> "Error whilst attempting to get future result - wrapping in AsyncException");
        }
    }

    private void waitForRegistration(CountDownLatch waitForRegistrationLatch) {
        try {
            waitForRegistrationLatch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.error("Exception occurred whilst awaiting registration of parent Future", e);
            throw new RuntimeException(e);
        }
    }
}
