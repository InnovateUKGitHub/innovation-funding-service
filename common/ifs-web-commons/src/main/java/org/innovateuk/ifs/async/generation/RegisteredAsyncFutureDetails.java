package org.innovateuk.ifs.async.generation;

import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;

/**
 * A holder of useful information about a Future and how it was created
 */
@EqualsAndHashCode
public class RegisteredAsyncFutureDetails<T> {

    private CompletableFuture<T> future;
    private String futureName;
    private AsyncFutureDetails parentFutureDetails;

    RegisteredAsyncFutureDetails(CompletableFuture<T> future, String futureName, AsyncFutureDetails asyncFutureDetails) {
        this.future = future;
        this.futureName = futureName;
        this.parentFutureDetails = asyncFutureDetails;
    }

    public CompletableFuture<T> getFuture() {
        return future;
    }

    public String getFutureName() {
        return futureName;
    }

    public List<String> getThreadAncestry() {
        return combineLists(parentFutureDetails.getThreadName(), parentFutureDetails.getThreadAncestry());
    }

    public List<String> getFutureAncestry() {
        return combineLists(parentFutureDetails.getFutureName(), parentFutureDetails.getFutureAncestry());
    }

}
