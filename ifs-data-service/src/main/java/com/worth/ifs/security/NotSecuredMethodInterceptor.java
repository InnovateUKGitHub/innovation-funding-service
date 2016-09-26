package com.worth.ifs.security;

import com.worth.ifs.commons.security.NotSecured;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * An interceptor for methods with the {@link NotSecured} annotation is called. Checks that if it is required that
 * another method above it in the stack is actually secured.
 */
@Component
public class NotSecuredMethodInterceptor implements MethodInterceptor {

    @Autowired
    private SecuredMethodsInStackCountInterceptor methodSecuredInStackCountInterceptor;

    private static final Logger LOG = LoggerFactory.getLogger(NotSecuredMethodInterceptor.class);
    private ThreadLocal<Boolean> isSecured = new ThreadLocal<>();

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        if (methodSecuredInStackCountInterceptor.isStackSecured()) {
            final Object proceed = invocation.proceed();
            return proceed;
        }
        else {
            throw new NotSecuredMethodException("This method expects to be secured further up the method chain");
        }
    }
}
