package org.innovateuk.ifs.async.generation.handler;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.async.generation.AsyncFuturesHolder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * A convenience subclass of {@link BaseCompletableFutureTupleHandler} that allows a developer to more concisely handle
 * the chaining of new CompletableFutures from the results of others.  It also handles the registering of the newly
 * chained Future with {@link AsyncFuturesHolder} to prevent the Controller from rendering any
 * templates until the new Future has completed.
 *
 * This subclass handles the chaining of 2 futures e.g in combination with
 * {@link AsyncFuturesGenerator#awaitAll(CompletableFuture,CompletableFuture)}:
 *
 * <pre>
 * {@code
 *
 * awaitAll(future1, future2).thenApply((future1Result, future2Result) -> doSomethingWith(future1Result, future2Result));
 *
 * }
 * </pre>
 */
public class CompletableFutureTuple2Handler<T1, T2> extends BaseCompletableFutureTupleHandler {

    public CompletableFutureTuple2Handler(String futureName, Executor threadPool, CompletableFuture<T1> future1, CompletableFuture<T2> future2) {
        super(futureName, threadPool, future1, future2);
    }

    public <R> CompletableFuture<R> thenApply(BiFunction<T1, T2, R> handler) {
        return thenApplyInternal(() -> handler.apply(getResult(0), getResult(1)));
    }

    public CompletableFuture<Void> thenAccept(BiConsumer<T1, T2> handler) {

        return thenApplyInternal(() -> {
            handler.accept(getResult(0), getResult(1));
            return null;
        });
    }

    public Pair<T1, T2> thenReturn() {
        waitForFuturesAndDescendantsToFullyComplete();
        return Pair.of(getResult(0), getResult(1));
    }
}
