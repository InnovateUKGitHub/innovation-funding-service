package org.innovateuk.ifs.async.util;

import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * A convenience subclass that provides delegate methods through to {@link AsyncFuturesGenerator}, for more concise writing of async
 * handling code.
 */
public abstract class AsyncAdaptor {

    @Autowired
    private AsyncFuturesGenerator asyncFuturesGenerator;

    public <T> CompletableFuture<T> async(Supplier<T> supplier) {
        return asyncFuturesGenerator.async(supplier);
    }

    public <T> CompletableFuture<T> async(String futureName, Supplier<T> supplier) {
        return asyncFuturesGenerator.async(futureName, supplier);
    }

    public CompletableFuture<Void> async(Runnable runnable) {
        return asyncFuturesGenerator.async(runnable);
    }

    public CompletableFuture<Void> async(String futureName, Runnable runnable) {
        return asyncFuturesGenerator.async(futureName, runnable);
    }

    public <R1> CompletableFutureTuple1Handler<R1> awaitAll(CompletableFuture<R1> future1) {
        return asyncFuturesGenerator.awaitAll(future1);
    }

    public <R1, R2> CompletableFutureTuple2Handler<R1, R2> awaitAll(CompletableFuture<R1> future1, CompletableFuture<R2> future2) {
        return asyncFuturesGenerator.awaitAll(future1, future2);
    }

    public <R1, R2, R3> CompletableFutureTuple3Handler<R1, R2, R3> awaitAll(CompletableFuture<R1> future1, CompletableFuture<R2> future2, CompletableFuture<R3> future3) {
        return asyncFuturesGenerator.awaitAll(future1, future2, future3);
    }

    public CompletableFutureTupleNHandler awaitAll(CompletableFuture<?> future1, CompletableFuture<?> future2, CompletableFuture<?> future3, CompletableFuture<?>... moreFutures) {
        return asyncFuturesGenerator.awaitAll(future1, future2, future3, moreFutures);
    }

    public <R1> CompletableFutureTuple1Handler<R1> awaitAll(String futureName, CompletableFuture<R1> future1) {
        return asyncFuturesGenerator.awaitAll(futureName, future1);
    }

    public <R1, R2> CompletableFutureTuple2Handler<R1, R2> awaitAll(String futureName, CompletableFuture<R1> future1, CompletableFuture<R2> future2) {
        return asyncFuturesGenerator.awaitAll(futureName, future1, future2);
    }

    public <R1, R2, R3> CompletableFutureTuple3Handler<R1, R2, R3> awaitAll(String futureName, CompletableFuture<R1> future1, CompletableFuture<R2> future2, CompletableFuture<R3> future3) {
        return asyncFuturesGenerator.awaitAll(futureName, future1, future2, future3);
    }

    public CompletableFutureTupleNHandler awaitAll(String futureName, CompletableFuture<?> future1, CompletableFuture<?> future2, CompletableFuture<?> future3, CompletableFuture<?>... moreFutures) {
        return asyncFuturesGenerator.awaitAll(futureName, future1, future2, future3, moreFutures);
    }

    public <T> T futureResult(CompletableFuture<T> future) {
        return asyncFuturesGenerator.futureResult(future);
    }
}
