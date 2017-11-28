package org.innovateuk.ifs.commons.security;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

/**
 * An interceptor that keeps track of whether a method has been secured at some point in the calling chain.
 */
@Component
public class SecuredMethodsInStackCountInterceptor implements MethodInterceptor {

    private ThreadLocal<Integer> securedMethodsInStackCount = new ThreadLocal<>();

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Integer numberSecuredMethodsInStackBeforeCalling = securedMethodsInStackCount.get() != null ? securedMethodsInStackCount.get() : 0;
        try {
            securedMethodsInStackCount.set(numberSecuredMethodsInStackBeforeCalling + 1);
            return invocation.proceed();
        } finally {
            securedMethodsInStackCount.set(numberSecuredMethodsInStackBeforeCalling);
        }
    }

    public boolean isStackSecured() {
        return securedMethodsInStackCount.get() != null && securedMethodsInStackCount.get() > 0;
    }

    public boolean isStackSecuredAtHigherLevel() {
        return securedMethodsInStackCount.get() != null && securedMethodsInStackCount.get() > 1;
    }
}
