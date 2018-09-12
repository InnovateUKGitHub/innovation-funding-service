package org.innovateuk.ifs.async.generation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.async.executor.AsyncExecutorFactory;
import org.innovateuk.ifs.async.generation.handler.*;
import org.innovateuk.ifs.async.util.AsyncAllowedThreadLocal;
import org.innovateuk.ifs.util.ExceptionThrowingRunnable;
import org.innovateuk.ifs.util.ExceptionThrowingSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.async.exceptions.AsyncException.getOriginalAsyncExceptionOrWrapInAsyncException;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;

/**
 * The entry mechanism for executing blocks of code asynchronously.  All code that wishes to execute multiple jobs in
 * parallel should use this class as the first port of call.
 *
 * The {@link AsyncFuturesGenerator} async() method is the main entrypoint to spawn async blocks of code, and this will return
 * a CompletableFuture that can be waited on e.g. async(() -> doSomethingAsynchronously());
 *
 * Additionally, there are multiple awaitAll() methods provided here that, given X futures to wait on, will return
 * a convenience wrapper object (a CompletableFutureTupleXHandler) that will allow the developer to use a handy
 * thenApply() or thenAccept() method to chain additional Future behaviour.  This Future behaviour method is passed an
 * appropriate tuple of X parameters after all X awaited futures have completed e.g.
 *
 * awaitAll(future1, future2).thenApply((result1, result2) -> doSomethingWith(result1, result2));
 *
 * All CompletableFutures generated via the async() and awaitAll() methods provided in this class will be registered
 * with {@link AsyncFuturesHolder} which will prevent Controllers from rendering templates until all of the Futures have
 * completed.
 */
@Component
public class AsyncFuturesGenerator {

    private static final Log LOG = LogFactory.getLog(AsyncFuturesGenerator.class);

    @Autowired
    private AsyncExecutorFactory executorFactory;

    /**
     * A self-wiring here to allow methods within this class to use the @Async proxy mechanisms put in place by Spring
     * on the {@link AsyncFuturesGenerator#asyncInternal} method.
     */
    private AsyncFuturesGenerator self;

    @Autowired
    @Lazy
    public AsyncFuturesGenerator(AsyncFuturesGenerator self) {
        this.self = self;
    }

    public <T> CompletableFuture<T> async(ExceptionThrowingSupplier<T> supplier) {
        return async(randomName(), supplier);
    }

    public <T> CompletableFuture<T> async(String futureName, ExceptionThrowingSupplier<T> supplier) {

        AsyncFutureDetails currentlyExecutingFuture = AsyncFuturesHolder.getCurrentlyExecutingFutureDetails() != null ?
                AsyncFuturesHolder.getCurrentlyExecutingFutureDetails() :
                AsyncFutureDetails.topLevelThread();

        ExceptionThrowingSupplier<T> decoratedSupplier = () -> {

            AsyncFuturesHolder.addCurrentFutureBeingProcessed(futureName, currentlyExecutingFuture);

            try {
                return supplier.get();
            } finally {
                AsyncFuturesHolder.clearCurrentFutureBeingProcessed();
            }
        };

        if (AsyncAllowedThreadLocal.isAsyncAllowed()) {

            // create and register the Future.  Once registered, release the Future to run
            CountDownLatch waitForRegistrationLatch = new CountDownLatch(1);

            CompletableFuture<T> asyncBlock = self.asyncInternal(() -> {
                waitForRegistrationLatch.await(1, TimeUnit.SECONDS);
                return decoratedSupplier.get();
            });

            CompletableFuture<T> registeredFuture = AsyncFuturesHolder.registerFuture(futureName, asyncBlock);
            waitForRegistrationLatch.countDown();
            return registeredFuture;

        } else {

            LOG.warn("Cannot process async block asynchronously - processing on the main Thread instead.  Annotate " +
                    "a method in the callstack with @AsyncMethod to enable asynchronous execution");

            return nonAsyncInternal(decoratedSupplier);
        }
    }

    public CompletableFuture<Void> async(ExceptionThrowingRunnable runnable) {
        return async(randomName(), runnable);
    }

    public CompletableFuture<Void> async(String futureName, ExceptionThrowingRunnable runnable) {

        ExceptionThrowingSupplier<Void> dummySupplier = () -> {
            try {
                runnable.run();
                return null;
            } catch (Exception e) {
                LOG.error("Error whilst processing Runnable Future", e);
                throw getOriginalAsyncExceptionOrWrapInAsyncException(e, () -> "Error whilst processing Runnable Future");
            }
        };

        return async(futureName, dummySupplier);
    }

    /**
     * Package-private to allow Spring to proxy this method
     */
    @Async
    <T> CompletableFuture<T> asyncInternal(ExceptionThrowingSupplier<T> supplier) {
        try {
            T value = supplier.get();
            return CompletableFuture.completedFuture(value);
        } catch (Exception e) {
            LOG.error("Error whilst processing Supplier Future", e);
            throw getOriginalAsyncExceptionOrWrapInAsyncException(e, () -> "Error whilst processing Supplier Future");
        }
    }

    <T> CompletableFuture<T> nonAsyncInternal(ExceptionThrowingSupplier<T> supplier) {
        try {
            T value = supplier.get();
            return CompletableFuture.completedFuture(value);
        } catch (Exception e) {
            LOG.error("Error whilst processing Supplier Future", e);
            throw getOriginalAsyncExceptionOrWrapInAsyncException(e, () -> "Error whilst processing Supplier Future");
        }
    }

    public <R1> CompletableFutureTuple1Handler<R1> awaitAll(CompletableFuture<R1> future1) {
        return awaitAll(randomName(), future1);
    }

    public <R1, R2> CompletableFutureTuple2Handler<R1, R2> awaitAll(CompletableFuture<R1> future1, CompletableFuture<R2> future2) {
        return awaitAll(randomName(), future1, future2);
    }

    public <R1, R2, R3> CompletableFutureTuple3Handler<R1, R2, R3> awaitAll(CompletableFuture<R1> future1, CompletableFuture<R2> future2, CompletableFuture<R3> future3) {
        return awaitAll(randomName(), future1, future2, future3);
    }

    public <R1, R2, R3, R4> CompletableFutureTuple4Handler<R1, R2, R3, R4> awaitAll(CompletableFuture<R1> future1, CompletableFuture<R2> future2, CompletableFuture<R3> future3, CompletableFuture<R4> future4) {
        return awaitAll(randomName(), future1, future2, future3, future4);
    }

    public CompletableFutureTupleNHandler awaitAll(CompletableFuture<?> future1, CompletableFuture<?> future2, CompletableFuture<?> future3, CompletableFuture<?> future4, CompletableFuture<?>... moreFutures) {
        return awaitAll(randomName(), future1, future2, future3, future4, moreFutures);
    }

    public CompletableFutureTupleNHandler awaitAll(List<? extends CompletableFuture<?>> futures) {
        return awaitAll(randomName(), futures);
    }

    public <R1> CompletableFutureTuple1Handler<R1> awaitAll(String futureName, CompletableFuture<R1> future1) {
        return new CompletableFutureTuple1Handler<>(futureName, getExecutorForChainedFutures(), future1);
    }

    public <R1, R2> CompletableFutureTuple2Handler<R1, R2> awaitAll(String futureName, CompletableFuture<R1> future1, CompletableFuture<R2> future2) {
        return new CompletableFutureTuple2Handler<>(futureName, getExecutorForChainedFutures(), future1, future2);
    }

    public <R1, R2, R3> CompletableFutureTuple3Handler<R1, R2, R3> awaitAll(String futureName, CompletableFuture<R1> future1, CompletableFuture<R2> future2, CompletableFuture<R3> future3) {
        return new CompletableFutureTuple3Handler<>(futureName, getExecutorForChainedFutures(), future1, future2, future3);
    }

    public <R1, R2, R3, R4> CompletableFutureTuple4Handler<R1, R2, R3, R4> awaitAll(String futureName, CompletableFuture<R1> future1, CompletableFuture<R2> future2, CompletableFuture<R3> future3, CompletableFuture<R4> future4) {
        return new CompletableFutureTuple4Handler<>(futureName, getExecutorForChainedFutures(), future1, future2, future3, future4);
    }

    public CompletableFutureTupleNHandler awaitAll(String futureName, CompletableFuture<?> future1, CompletableFuture<?> future2, CompletableFuture<?> future3, CompletableFuture<?> future4, CompletableFuture<?>... moreFutures) {
        List<CompletableFuture<?>> allFutures = combineLists(asList(future1, future2, future3, future4), moreFutures);
        return new CompletableFutureTupleNHandler(futureName, getExecutorForChainedFutures(), allFutures);
    }

    public CompletableFutureTupleNHandler awaitAll(String futureName, List<? extends CompletableFuture<?>> futures) {
        return new CompletableFutureTupleNHandler(futureName, getExecutorForChainedFutures(), futures);
    }

    private String randomName() {
        return UUID.randomUUID().toString();
    }

    /**
     * Choose an appropriate Task Executor for executing any chained futures.  If sleuth is enabled, the
     * SleuthExecutorFactory will be used to create an Executor, allowing child threads to record their
     * data service calls under a parent thread's Span.  Otherwise, a default Executor will be used.
     */
    private Executor getExecutorForChainedFutures() {
        return executorFactory.createAsyncExecutor();
    }
}