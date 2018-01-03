package org.innovateuk.ifs.async.controller;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.async.generation.AsyncFuturesHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import static org.innovateuk.ifs.application.service.Futures.callAllFutures;

@Component
public class AwaitAllFuturesCompletionMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        // call the Controller method
        Object proceed = invocation.proceed();

        /**
         * Wait for all CompletableFutures generated via {@link AsyncFuturesGenerator} or manually registered with
         * {@link AsyncFuturesHolder} in some other way to complete before allowing the Controller to return
         */
        AsyncFuturesHolder.waitForAllFuturesToComplete();

        /**
         * Wait for any Futures added directly to the Spring Model to complete before allowing the Controller to return
         */
        for (Object argument : invocation.getArguments()) {
            if (argument instanceof Model) {
                callAllFutures((Model) argument);
            }
        }

        return proceed;
    }
}
