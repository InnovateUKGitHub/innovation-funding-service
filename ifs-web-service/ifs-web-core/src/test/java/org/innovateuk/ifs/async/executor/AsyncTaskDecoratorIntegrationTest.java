package org.innovateuk.ifs.async.executor;

import org.innovateuk.ifs.async.AsyncExecutionTestHelper;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

/**
 * Tests to ensure that the {@link AsyncTaskDecorator} is being applied successfully to threads that are executing @Async
 * blocks of code.  The main job of the {@link AsyncTaskDecorator} is to transfer important ThreadLocals from a Thread
 * to any child Threads that it initiates via @Async code block execution.
 */
public class AsyncTaskDecoratorIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AsyncExecutionTestHelper helper;

    @Test
    public void testSpringSecurityContextTransferredToChildThread() throws ExecutionException, InterruptedException {

        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);

        CompletableFuture<Void> futureAssertion = helper.executeAsync(() -> {

            assertSame(context, SecurityContextHolder.getContext());

            CompletableFuture<Void> futureAssertion2 = helper.executeAsync(() ->
                    assertSame(context, SecurityContextHolder.getContext()));

            futureAssertion2.get();
        });

        futureAssertion.get();
    }

}
