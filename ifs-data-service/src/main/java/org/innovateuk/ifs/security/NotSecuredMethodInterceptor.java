package org.innovateuk.ifs.security;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredMethodsInStackCountInterceptor;
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

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        if (methodSecuredInStackCountInterceptor.isStackSecured()) {
            return invocation.proceed();
        } else {
            throw new NotSecuredMethodException("This method expects to be secured further up the method chain");
        }
    }
}
