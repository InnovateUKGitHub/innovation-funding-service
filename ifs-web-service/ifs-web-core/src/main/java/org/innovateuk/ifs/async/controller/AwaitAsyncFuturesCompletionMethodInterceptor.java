package org.innovateuk.ifs.async.controller;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.async.generation.AsyncFuturesHolder;
import org.innovateuk.ifs.async.util.AsyncAllowedThreadLocal;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.async.exceptions.AsyncException.unwrapOriginalExceptionFromAsyncException;

/**
 * This method interceptor targets methods annotated with {@link org.innovateuk.ifs.async.annotations.AsyncMethod} and
 * ensures that any Futures created via {@link  AsyncFuturesGenerator} (and any
 * descendant Futures) are completed before the method completes.  Typically this will be used by Controller
 * request-handling methods that require some async
 * support.
 */
@Component
public class AwaitAsyncFuturesCompletionMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        boolean asyncCurrentlyAllowed = false;

        try {

            asyncCurrentlyAllowed = AsyncAllowedThreadLocal.isAsyncAllowed();

            AsyncAllowedThreadLocal.setAsyncAllowed(true);

            // call the Controller method
            Object returnValue = invocation.proceed();

            //
            // Wait for all CompletableFutures generated via {@link AsyncFuturesGenerator} or manually registered with
            // {@link AsyncFuturesHolder} in some other way to complete before allowing the Controller to return
            //
            AsyncFuturesHolder.waitForAllFuturesToComplete();

            return returnValue;

        } catch (Exception e) {

            // upon an exception occurring, cancel and clear out all currently executing Futures for this Thread
            AsyncFuturesHolder.cancelAndClearFutures();

            throw unwrapOriginalExceptionFromAsyncException(e);

        } finally {

            AsyncAllowedThreadLocal.setAsyncAllowed(asyncCurrentlyAllowed);

            // ensure that this Thread is clear of registered Futures
            AsyncFuturesHolder.clearFutures();
        }
    }
}
