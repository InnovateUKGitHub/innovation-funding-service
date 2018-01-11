package org.innovateuk.ifs.async.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.async.controller.AsyncAllowedThreadLocal;
import org.innovateuk.ifs.async.exceptions.AsyncException;
import org.innovateuk.ifs.async.generation.AsyncFuturesHolder;
import org.innovateuk.ifs.util.ExceptionThrowingSupplier;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
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
            CompletableFuture<R> blockingFuture = joiningFuture.thenApplyAsync(done -> waitingSupplier.get(), threadPool);
            return AsyncFuturesHolder.registerFuture(futureName, blockingFuture);
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

    @SuppressWarnings("unchecked")
    private <R> R getResult(CompletableFuture<?> future)  {
        try {
            return (R) future.get();
        } catch (Exception e) {
            LOG.error("Error whilst attempting to get future result", e);
            throw AsyncException.getOriginalAsyncExceptionOrWrapInAsyncException(e, () -> "Error whilst attempting to get future result - wrapping in AsyncException");
        }
    }

    List<?> getResultsAsList()  {
        return simpleMap(futures, this::getResult);
    }
}
