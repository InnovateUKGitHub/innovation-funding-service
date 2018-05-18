package org.innovateuk.ifs.async;

import org.innovateuk.ifs.util.ExceptionThrowingRunnable;
import org.innovateuk.ifs.util.ExceptionThrowingSupplier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Test helper to allow execution of @Async code blocks
 */
@Component
public class AsyncExecutionTestHelper {

    /**
     * Amount of time to block an operation for in unit tests before timing them out.  This is to ensure that an operation
     * is definitely blocked by another for a decent amount of time that is higher than the amount of time it would take
     * to run that operaton unblocked when the tests were running slowly.  In other words, this variable has to be high
     * enough to deal with tests running slowly and throwing timeout exceptions because of genuine timeouts rather than
     * timeouts due to slow processing.
     */
    public static final long BLOCKING_TIMEOUT_MILLIS = 1000L;

    @Async
    public <T> CompletableFuture<T> executeAsync(ExceptionThrowingSupplier<T> supplier) {
        try {
            return CompletableFuture.completedFuture(supplier.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    public CompletableFuture<Void> executeAsync(ExceptionThrowingRunnable runnable) {
        try {
            runnable.run();
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
