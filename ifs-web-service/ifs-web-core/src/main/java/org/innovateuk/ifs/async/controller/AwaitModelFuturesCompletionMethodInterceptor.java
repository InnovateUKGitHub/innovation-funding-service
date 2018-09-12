package org.innovateuk.ifs.async.controller;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import static org.innovateuk.ifs.application.service.Futures.callAllFutures;

/**
 * This method interceptor targets request-handling Controller methods and ensures that any Futures created via
 * {@link AsyncFuturesGenerator} (and any descendant Futures) are completed before
 * the Controller completes.
 */
@Component
public class AwaitModelFuturesCompletionMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        // call the Controller method
        Object returnValue = invocation.proceed();

        //
        // Wait for any Futures added directly to the Spring Model to complete before allowing the Controller to return
        //
        for (Object argument : invocation.getArguments()) {
            if (argument instanceof Model) {
                callAllFutures((Model) argument);
            }
        }

        return returnValue;
    }
}
