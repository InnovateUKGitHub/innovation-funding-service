package org.innovateuk.ifs.async.generation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;

/**
 * A holder of useful information about a Future and how it was created
 */
public class RegisteredAsyncFutureDetails<T> {

    private CompletableFuture<T> future;
    private String futureName;
    private AsyncFutureDetails parentFutureDetails;

    public RegisteredAsyncFutureDetails(CompletableFuture<T> future, String futureName, AsyncFutureDetails asyncFutureDetails) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RegisteredAsyncFutureDetails<?> that = (RegisteredAsyncFutureDetails<?>) o;

        return new EqualsBuilder()
                .append(future, that.future)
                .append(futureName, that.futureName)
                .append(parentFutureDetails, that.parentFutureDetails)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(future)
                .append(futureName)
                .append(parentFutureDetails)
                .toHashCode();
    }
}
