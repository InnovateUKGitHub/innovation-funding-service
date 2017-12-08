package org.innovateuk.ifs.async.util;

import org.innovateuk.ifs.async.generation.AsyncFuturesHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A convenience class for maintaining a list of Futures to then await the completion of, to make code writing a bit
 * more succinct
 */
public class FuturesList {

    private List<CompletableFuture<?>> futures = new ArrayList<>();

    public <T> CompletableFuture<T> add(CompletableFuture<T> future) {
        futures.add(future);
        return future;
    }

    public void awaitAll() {
        AsyncFuturesHolder.waitForFuturesAndChildFuturesToCompleteFrom(futures);
    }
}
