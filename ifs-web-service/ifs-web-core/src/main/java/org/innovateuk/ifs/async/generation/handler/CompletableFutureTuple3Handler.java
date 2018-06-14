package org.innovateuk.ifs.async.generation.handler;

import org.apache.commons.lang3.tuple.Triple;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.async.generation.AsyncFuturesHolder;
import org.innovateuk.ifs.util.TriConsumer;
import org.innovateuk.ifs.util.TriFunction;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * A convenience subclass of {@link BaseCompletableFutureTupleHandler} that allows a developer to more concisely handle
 * the chaining of new CompletableFutures from the results of others.  It also handles the registering of the newly
 * chained Future with {@link AsyncFuturesHolder} to prevent the Controller from rendering any
 * templates until the new Future has completed.
 *
 * This subclass handles the chaining of 3 futures e.g in combination with
 * {@link AsyncFuturesGenerator#awaitAll(CompletableFuture,CompletableFuture,CompletableFuture)}:
 *
 * <pre>
 * {@code
 *
 * awaitAll(future1, future2, future3).thenApply((future1Result, future2Result, future3Result) ->
 *          doSomethingWith(future1Result, future2Result, future3Result));
 * }
 * </pre>
 */
public class CompletableFutureTuple3Handler<T1, T2, T3> extends BaseCompletableFutureTupleHandler {

    public CompletableFutureTuple3Handler(String futureName, Executor threadPool, CompletableFuture<T1> future1, CompletableFuture<T2> future2, CompletableFuture<T3> future3) {
        super(futureName, threadPool, future1, future2, future3);
    }

    public <R> CompletableFuture<R> thenApply(TriFunction<T1, T2, T3, R> handler) {
        return thenApplyInternal(() -> handler.apply(getResult(0), getResult(1), getResult(2)));
    }

    public CompletableFuture<Void> thenAccept(TriConsumer<T1, T2, T3> handler) {

        return thenApplyInternal(() -> {
            handler.accept(getResult(0), getResult(1), getResult(2));
            return null;
        });
    }

    public Triple<T1, T2, T3> thenReturn() {
        waitForFuturesAndDescendantsToFullyComplete();
        return Triple.of(getResult(0), getResult(1), getResult(2));
    }
}
