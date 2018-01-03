package org.innovateuk.ifs.async.util;

import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.async.generation.AsyncFuturesHolder;
import org.innovateuk.ifs.util.ExceptionThrowingConsumer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * A convenience subclass of {@link BaseCompletableFutureTupleHandler} that allows a developer to more concisely handle
 * the chaining of new CompletableFutures from the results of others.  It also handles the registering of the newly
 * chained Future with {@link AsyncFuturesHolder} to prevent the Controller from rendering any
 * templates until the new Future has completed.
 *
 * This subclass handles the chaining of 1 future e.g in combination with
 * {@link AsyncFuturesGenerator#awaitAll(CompletableFuture)}:
 *
 * <pre>
 * {@code
 *
 * awaitAll(future).thenApply(futureResult -> doSomethingWith(futureResult));
 *
 * }
 * </pre>
 */
public class CompletableFutureTuple1Handler<T1> extends BaseCompletableFutureTupleHandler {

    public CompletableFutureTuple1Handler(CompletableFuture<T1> future1) {
        this(UUID.randomUUID().toString(), future1);
    }

    public CompletableFutureTuple1Handler(String futureName, CompletableFuture<T1> future1) {
        super(futureName, future1);
    }

    public <R> CompletableFuture<R> thenApply(Function<T1, R> handler) {
        return thenApplyInternal(() -> handler.apply(getResult(0)));
    }

    public <R> CompletableFuture<Void> thenAccept(ExceptionThrowingConsumer<T1> handler) {

        return thenApplyInternal(() -> {
            handler.accept(getResult(0));
            return null;
        });
    }

    public T1 thenReturn() {
        waitForFuturesAndDescendantsToFullyComplete();
        return getResult(0);
    }
}
