package org.innovateuk.ifs.async.generation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.async.util.AsyncAllowedThreadLocal;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.async.exceptions.AsyncException.getOriginalAsyncExceptionOrWrapInAsyncException;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * This class holds per-Thread a list of CompletableFutures generated from serving an HTTP request, and it is this
 * class' main responsibility to keep a track of CompletableFutures that have been generated from a thread of execution
 * and to allow the application to  wait for the completion of all of those Futures prior to continuing execution.
 *
 * CompletableFutures generated through {@link AsyncFuturesGenerator} async() and awaitAll() methods will be
 * auto-registered with this class.
 *
 * Starting with the main execution thread for handing an HttpServletRequest via a Controller method, any
 * CompletableFutures generated from this Thread will be registered here.  Any child Futures of these registered Futures
 * will also be registered here and the same for the children's' children's Futures etc.
 *
 * As the {@link Queue} holding all of these Futures was created in the main HttpServletRequest-handling Thread
 * and passed down to child Threads, child's child Threads etc for them to also register their generated futures in,
 * we are therefore able to await the completion of all Futures, child Futures etc back in the main
 * HttpServletRequest-handling Thread so that we can halt rendering of any templates from the Controllers until all
 * Futures have completed.
 */
public final class AsyncFuturesHolder {

    private static final Log LOG = LogFactory.getLog(AsyncFuturesGenerator.class);

    private static final ThreadLocal<Queue<RegisteredAsyncFutureDetails<?>>> ASYNC_FUTURES = new ThreadLocal<>();
    private static final ThreadLocal<AsyncFutureDetails> CURRENTLY_EXECUTING_ASYNC_FUTURE = new ThreadLocal<>();

    private AsyncFuturesHolder() {}

    /**
     * This method, given a future, will register this future as a future to be tracked by this Thread (and by the parent
     * Thread of this Thread) so that we can await its completion should we wish to.
     *
     * @param futureName
     * @param future
     * @param <T>
     * @return
     */
    public static <T> CompletableFuture<T> registerFuture(String futureName, CompletableFuture<T> future) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("Registering Future " + futureName + " on Thread " + Thread.currentThread().getName());
        }

        AsyncFutureDetails currentlyExecutingFuture = CURRENTLY_EXECUTING_ASYNC_FUTURE.get();

        RegisteredAsyncFutureDetails<T> newFutureToBeProcessed;

        if (currentlyExecutingFuture != null) {
            newFutureToBeProcessed = new RegisteredAsyncFutureDetails<>(future, futureName, currentlyExecutingFuture);
        } else {
            AsyncFutureDetails topLevelDetails = AsyncFutureDetails.topLevelThread();
            newFutureToBeProcessed = new RegisteredAsyncFutureDetails<>(future, futureName, topLevelDetails);
        }

        getFuturesOrInitialise().add(newFutureToBeProcessed);

        return future;
    }

    static void addCurrentFutureBeingProcessed(String futureName, AsyncFutureDetails parentFuture) {
        List<String> newThreadAncestry = combineLists(parentFuture.getThreadAncestry(), parentFuture.getThreadName());
        List<String> newFutureAncestry = combineLists(parentFuture.getFutureAncestry(), parentFuture.getFutureName());
        AsyncFutureDetails thisFutureInformation = new AsyncFutureDetails(futureName, Thread.currentThread().getName(), newThreadAncestry, newFutureAncestry);
        CURRENTLY_EXECUTING_ASYNC_FUTURE.set(thisFutureInformation);
    }

    static AsyncFutureDetails getCurrentlyExecutingFutureDetails() {
        return CURRENTLY_EXECUTING_ASYNC_FUTURE.get();
    }

    static void clearCurrentFutureBeingProcessed() {
        CURRENTLY_EXECUTING_ASYNC_FUTURE.remove();
    }

    /**
     * @return the current set of ongoing Futures registered against this Thread
     */
    public static Queue<RegisteredAsyncFutureDetails<?>> getFuturesOrInitialise() {
        if (ASYNC_FUTURES.get() == null) {
            ASYNC_FUTURES.set(new LinkedBlockingQueue<>());
        }
        return ASYNC_FUTURES.get();
    }

    public static void cancelAndClearFutures() {

        Queue<RegisteredAsyncFutureDetails<?>> currentlyRegisteredFutures = ASYNC_FUTURES.get();

        if (currentlyRegisteredFutures == null) {
            return;
        }

        currentlyRegisteredFutures.forEach(future -> {
            try {
                future.getFuture().cancel(true);
            } catch (Exception e) {
                LOG.warn("Exception caught whilst cancelling a Future - continuing to cancel other Futures", e);
            }
        });

        clearFutures();
    }

    /**
     * @param futures - a set of ongoing Futures to set on this Thread
     */
    public static void setFutures(Queue<RegisteredAsyncFutureDetails<?>> futures) {
        ASYNC_FUTURES.set(futures);
    }

    /**
     * Clear out the set of ongoing Futures on this Thread
     */
    public static void clearFutures() {
        ASYNC_FUTURES.remove();
    }

    /**
     * This method takes the set of ongoing Futures generated by this Thread and child Threads, child child Threads etc
     * and allows us to wait for them to complete before proceeding.
     *
     * Most importantly, this method is used within {@link org.innovateuk.ifs.async.controller.AwaitModelFuturesCompletionMethodInterceptor}
     * to prevent a Controller handler method from fully completing and going on to render a template until all Futures
     * generated by that Controller and its child Future threads, and any of their children, have completed.
     */
    public static void waitForAllFuturesToComplete() {

        try {
            waitForFuturesAndChildFuturesToCompleteByFutureName(singletonList("Top level"));
        } finally {
            clearFutures();
        }
    }

    /**
     * Allows us to block on the completion of the given CompletableFutures and all descendant Futures, providing that
     * these CompletableFutures and the descendant Futures have been registered via
     * {@link AsyncFuturesHolder#registerFuture(String, CompletableFuture)}
     */
    public static void waitForFuturesAndChildFuturesToCompleteFrom(List<? extends CompletableFuture<?>> futuresToBlockOn) {

        Queue<RegisteredAsyncFutureDetails<?>> futures = ASYNC_FUTURES.get();

        // if we're not currently allowing async execution then there is nothing to wait on, so simply return
        // immediatedy
        if (!AsyncAllowedThreadLocal.isAsyncAllowed()) {
            assert futures == null || futures.isEmpty();
            return;
        }

        List<RegisteredAsyncFutureDetails<?>> futureDetails = simpleFilter(futures, f -> futuresToBlockOn.contains(f.getFuture()));

        if (futureDetails.size() != futuresToBlockOn.size()) {

            throw new IllegalArgumentException("Unable to find all futures registered in order to block on their " +
                    "completion (and their descendant futures).  Wanted to block on " + futuresToBlockOn.size() +
                    " futures but were they registered via AsyncFuturesHolder.registerFuture()?");
        }

        waitForFuturesAndChildFuturesToCompleteByFutureName(simpleMap(futureDetails, RegisteredAsyncFutureDetails::getFutureName));
    }

    /**
     * This method allows us to block awaiting the completion of one or more Futures and their child Futures, children's
     * child Futures etc
     */
    private static void waitForFuturesAndChildFuturesToCompleteByFutureName(List<String> futureNames) {

        Queue<RegisteredAsyncFutureDetails<?>> futures = ASYNC_FUTURES.get();

        if (futures == null || futures.isEmpty()) {
            return;
        }

        List<RegisteredAsyncFutureDetails<?>> completedFutures = new ArrayList<>();
        List<RegisteredAsyncFutureDetails<?>> futuresSpawnedFromTheseProcesses;

        do {

            futuresSpawnedFromTheseProcesses = simpleFilter(futures,
                    f -> simpleAnyMatch(f.getFutureAncestry(), futureNames::contains));

            futuresSpawnedFromTheseProcesses.removeAll(completedFutures);

            if (!futuresSpawnedFromTheseProcesses.isEmpty()) {

                List<CompletableFuture<?>> actualFutures =
                        simpleMap(futuresSpawnedFromTheseProcesses, RegisteredAsyncFutureDetails::getFuture);

                CompletableFuture<Void> futureBatch =
                        CompletableFuture.allOf(actualFutures.toArray(new CompletableFuture<?>[futuresSpawnedFromTheseProcesses.size()]));

                try {
                    // TODO DW - add configuration for this
                    futureBatch.get(600, TimeUnit.SECONDS);
                } catch (Exception e) {
                    LOG.error("Exception caught whilst waiting for all futures to complete on main thread", e);

                    throw getOriginalAsyncExceptionOrWrapInAsyncException(e, () -> "Exception caught whilst waiting for all futures " +
                            "to complete on main thread - wrapping in AsyncException");
                }

                completedFutures.addAll(futuresSpawnedFromTheseProcesses);
            }

        } while (!futuresSpawnedFromTheseProcesses.isEmpty());
    }
}
