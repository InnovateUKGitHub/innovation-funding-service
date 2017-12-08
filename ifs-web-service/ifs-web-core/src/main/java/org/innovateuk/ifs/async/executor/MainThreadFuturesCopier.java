package org.innovateuk.ifs.async.executor;

import org.innovateuk.ifs.async.generation.AsyncFuturesHolder;
import org.innovateuk.ifs.async.generation.RegisteredAsyncFutureDetails;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This {@link AsyncThreadLocalCopier} implementation is responsible for making available the ConcurrentLinkedQueue of
 * CompletableFutures that the main Controller thread is going to need to block and wait upon completion of, before
 * safely being able to go ahead and render the response page (this is to allow all in-flight Futures i.e. @Async
 * execution blocks to complete and add any information they have to the Model prior to attempting to render the page).
 */
@Component
public class MainThreadFuturesCopier implements AsyncThreadLocalCopier<ConcurrentLinkedQueue<RegisteredAsyncFutureDetails>> {

    @Override
    public ConcurrentLinkedQueue<RegisteredAsyncFutureDetails> getOriginalValueFromOriginalThread() {
        return AsyncFuturesHolder.getFuturesOrInitialise();
    }

    @Override
    public void clearCopiedValueFromAsyncThread() {
        AsyncFuturesHolder.clearFutures();
    }

    @Override
    public void setCopyOfOriginalValueOnAsyncThread(ConcurrentLinkedQueue<RegisteredAsyncFutureDetails> originalValue) {
        AsyncFuturesHolder.setFutures(originalValue);
    }
}
