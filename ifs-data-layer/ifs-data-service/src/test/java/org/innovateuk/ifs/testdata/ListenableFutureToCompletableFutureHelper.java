package org.innovateuk.ifs.testdata;

import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.CompletableFuture;

/**
 * TODO DW - document this class
 */
public class ListenableFutureToCompletableFutureHelper {

    /**
     * This method converts a ListenableFuture into a CompletableFuture.  CompletableFutures are useful to us because
     * it allows us to chain futures together to produce new compound Futures.
     *
     * Originally from https://blog.krecan.net/2014/06/11/converting-listenablefutures-to-completablefutures-and-back/
     */
    public static <T> CompletableFuture<T> future(ListenableFuture<T> listenableFuture) {

        //create an instance of CompletableFuture
        CompletableFuture<T> completable = new CompletableFuture<T>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                // propagate cancel to the listenable future
                boolean result = listenableFuture.cancel(mayInterruptIfRunning);
                super.cancel(mayInterruptIfRunning);
                return result;
            }
        };

        // add callback
        listenableFuture.addCallback(new ListenableFutureCallback<T>() {
            @Override
            public void onSuccess(T result) {
                completable.complete(result);
            }

            @Override
            public void onFailure(Throwable t) {
                completable.completeExceptionally(t);
            }
        });
        return completable;
    }
}
