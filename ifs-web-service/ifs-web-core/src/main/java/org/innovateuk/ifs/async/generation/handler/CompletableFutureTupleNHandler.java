package org.innovateuk.ifs.async.generation.handler;

import org.innovateuk.ifs.async.exceptions.AsyncException;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.async.generation.AsyncFuturesHolder;
import org.innovateuk.ifs.commons.service.ExceptionThrowingFunction;
import org.innovateuk.ifs.util.ExceptionThrowingConsumer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * A convenience subclass of {@link BaseCompletableFutureTupleHandler} that allows a developer to more concisely handle
 * the chaining of new CompletableFutures from the results of others.  It also handles the registering of the newly
 * chained Future with {@link AsyncFuturesHolder} to prevent the Controller from rendering any
 * templates until the new Future has completed.
 *
 * This subclass handles the chaining of more than 3 futures e.g in combination with
 * {@link AsyncFuturesGenerator#awaitAll(CompletableFuture,CompletableFuture,CompletableFuture,CompletableFuture...)}:
 *
 * <pre>
 * {@code
 *
 * awaitAll(future1, future2, future3, future4, future5).thenApply(() -> doSomething());
 * }
 * </pre>
 *
 * This subclass is slightly less generous than other {@link BaseCompletableFutureTupleHandler} subclasses in that it
 * does not supply the results of the futures explicitly to the handler methods passed to
 * {@link CompletableFutureTupleNHandler#thenAccept(ExceptionThrowingConsumer)} or
 * {@link CompletableFutureTupleNHandler#thenApply(ExceptionThrowingFunction)}
 */
public class CompletableFutureTupleNHandler extends BaseCompletableFutureTupleHandler {

    public CompletableFutureTupleNHandler(String futureName, Executor threadPool, List<? extends CompletableFuture<?>> futures) {
        super(futureName, threadPool, futures.toArray(new CompletableFuture<?>[] {}));
    }

    public <R> CompletableFuture<R> thenApply(ExceptionThrowingFunction<List<?>, R> handler) {
        return thenApplyInternal(() -> {
            try {
                return handler.apply(getResultsAsList());
            } catch (Exception e) {
                throw AsyncException.getOriginalAsyncExceptionOrWrapInAsyncException(e, () -> "Error whilst applying Future handler");
            }
        });
    }

    public CompletableFuture<Void> thenAccept(ExceptionThrowingConsumer<List<?>> runnable) {

        ExceptionThrowingFunction<List<?>, Void> dummyFunction = futureResults -> {
            runnable.accept(futureResults);
            return null;
        };

        return thenApply(dummyFunction);
    }

    public void thenReturn() {
        waitForFuturesAndDescendantsToFullyComplete();
    }
}
