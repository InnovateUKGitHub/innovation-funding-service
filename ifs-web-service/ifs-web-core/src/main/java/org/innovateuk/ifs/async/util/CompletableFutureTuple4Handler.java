package org.innovateuk.ifs.async.util;

import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.async.generation.AsyncFuturesHolder;
import org.innovateuk.ifs.util.Quad;
import org.innovateuk.ifs.util.QuadConsumer;
import org.innovateuk.ifs.util.QuadFunction;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * A convenience subclass of {@link BaseCompletableFutureTupleHandler} that allows a developer to more concisely handle
 * the chaining of new CompletableFutures from the results of others.  It also handles the registering of the newly
 * chained Future with {@link AsyncFuturesHolder} to prevent the Controller from rendering any
 * templates until the new Future has completed.
 *
 * This subclass handles the chaining of 4 futures e.g in combination with
 * {@link AsyncFuturesGenerator#awaitAll(CompletableFuture,CompletableFuture,CompletableFuture,CompletableFuture)}:
 *
 * <pre>
 * {@code
 *
 * awaitAll(future1, future2, future3, future4).thenApply((future1Result, future2Result, future3Result, future4Result) ->
 *          doSomethingWith(future1Result, future2Result, future3Result, future4Result));
 * }
 * </pre>
 */
public class CompletableFutureTuple4Handler<T1, T2, T3, T4> extends BaseCompletableFutureTupleHandler {

    public CompletableFutureTuple4Handler(String futureName, Executor threadPool, CompletableFuture<T1> future1, CompletableFuture<T2> future2, CompletableFuture<T3> future3, CompletableFuture<T4> future4) {
        super(futureName, threadPool, future1, future2, future3, future4);
    }

    public <R> CompletableFuture<R> thenApply(QuadFunction<T1, T2, T3, T4, R> handler) {
        return thenApplyInternal(() -> handler.apply(getResult(0), getResult(1), getResult(2), getResult(3)));
    }

    public CompletableFuture<Void> thenAccept(QuadConsumer<T1, T2, T3, T4> handler) {

        return thenApplyInternal(() -> {
            handler.accept(getResult(0), getResult(1), getResult(2), getResult(3));
            return null;
        });
    }

    public Quad<T1, T2, T3, T4> thenReturn() {
        waitForFuturesAndDescendantsToFullyComplete();
        return Quad.of(getResult(0), getResult(1), getResult(2), getResult(3));
    }
}
