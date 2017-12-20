package org.innovateuk.ifs.async.config;

import org.innovateuk.ifs.util.ExceptionThrowingSupplier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Test helper for {@link AsyncMechanismIntegrationTest} to allow execution of @Async code blocks
 */
@Component
class AsyncMechanismIntegrationTestHelper {

    @Async
    <T> CompletableFuture<T> executeAsync(ExceptionThrowingSupplier<T> supplier) {
        try {
            return CompletableFuture.completedFuture(supplier.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
