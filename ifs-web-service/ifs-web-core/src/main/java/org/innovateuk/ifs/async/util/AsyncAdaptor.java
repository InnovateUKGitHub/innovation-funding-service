package org.innovateuk.ifs.async.util;

import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.async.generation.AsyncFuturesHolder;
import org.innovateuk.ifs.util.ExceptionThrowingRunnable;
import org.innovateuk.ifs.util.ExceptionThrowingSupplier;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A convenience subclass that provides delegate methods through to {@link AsyncFuturesGenerator}, for more concise writing of async
 * handling code.
 */
public abstract class AsyncAdaptor {

    @Autowired
    private AsyncFuturesGenerator asyncFuturesGenerator;

    protected <T> CompletableFuture<T> async(ExceptionThrowingSupplier<T> supplier) {
        return asyncFuturesGenerator.async(supplier);
    }

    protected <T> CompletableFuture<T> async(String futureName, ExceptionThrowingSupplier<T> supplier) {
        return asyncFuturesGenerator.async(futureName, supplier);
    }

    protected CompletableFuture<Void> async(ExceptionThrowingRunnable runnable) {
        return asyncFuturesGenerator.async(runnable);
    }

    protected CompletableFuture<Void> async(String futureName, ExceptionThrowingRunnable runnable) {
        return asyncFuturesGenerator.async(futureName, runnable);
    }

    protected <R1> CompletableFutureTuple1Handler<R1> awaitAll(CompletableFuture<R1> future1) {
        return asyncFuturesGenerator.awaitAll(future1);
    }

    protected <R1, R2> CompletableFutureTuple2Handler<R1, R2> awaitAll(CompletableFuture<R1> future1, CompletableFuture<R2> future2) {
        return asyncFuturesGenerator.awaitAll(future1, future2);
    }

    protected <R1, R2, R3> CompletableFutureTuple3Handler<R1, R2, R3> awaitAll(CompletableFuture<R1> future1, CompletableFuture<R2> future2, CompletableFuture<R3> future3) {
        return asyncFuturesGenerator.awaitAll(future1, future2, future3);
    }

    protected <R1, R2, R3, R4> CompletableFutureTuple4Handler<R1, R2, R3, R4> awaitAll(CompletableFuture<R1> future1, CompletableFuture<R2> future2, CompletableFuture<R3> future3, CompletableFuture<R4> future4) {
        return asyncFuturesGenerator.awaitAll(future1, future2, future3, future4);
    }

    protected CompletableFutureTupleNHandler awaitAll(CompletableFuture<?> future1, CompletableFuture<?> future2, CompletableFuture<?> future3, CompletableFuture<?>... moreFutures) {
        return asyncFuturesGenerator.awaitAll(future1, future2, future3, moreFutures);
    }

    protected CompletableFutureTupleNHandler awaitAll(List<? extends CompletableFuture<?>> futures) {
        return asyncFuturesGenerator.awaitAll(futures);
    }

    protected <R1> CompletableFutureTuple1Handler<R1> awaitAll(String futureName, CompletableFuture<R1> future1) {
        return asyncFuturesGenerator.awaitAll(futureName, future1);
    }

    protected <R1, R2> CompletableFutureTuple2Handler<R1, R2> awaitAll(String futureName, CompletableFuture<R1> future1, CompletableFuture<R2> future2) {
        return asyncFuturesGenerator.awaitAll(futureName, future1, future2);
    }

    protected <R1, R2, R3> CompletableFutureTuple3Handler<R1, R2, R3> awaitAll(String futureName, CompletableFuture<R1> future1, CompletableFuture<R2> future2, CompletableFuture<R3> future3) {
        return asyncFuturesGenerator.awaitAll(futureName, future1, future2, future3);
    }

    protected CompletableFutureTupleNHandler awaitAll(String futureName, CompletableFuture<?> future1, CompletableFuture<?> future2, CompletableFuture<?> future3, CompletableFuture<?>... moreFutures) {
        return asyncFuturesGenerator.awaitAll(futureName, future1, future2, future3, moreFutures);
    }

    protected void waitForFuturesAndChildFuturesToCompleteFrom(List<? extends CompletableFuture<?>> futures) {
        AsyncFuturesHolder.waitForFuturesAndChildFuturesToCompleteFrom(futures);
    }
}
