package com.worth.ifs.security;

import com.worth.ifs.commons.interceptor.AbstractIfsMethodsAdvice;
import com.worth.ifs.commons.security.NotSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.function.BiFunction;

/**
 * Advice to be called on methods marked as {@link NotSecured} with {@link NotSecured#mustBeSecuredByOtherServices()}
 * set to true.
 */
@Component
public class NotSecuredMethodThatMustBeSecuredByOtherMethodsAdvice extends AbstractIfsMethodsAdvice {

    public static final int NOT_SECURED_METHOD_THAT_MUST_BE_SECURED_BY_OTHER_METHODS_ORDER = Ordered.LOWEST_PRECEDENCE - 1;
    private static final long serialVersionUID = 1L;
    private static final BiFunction<Method, Class, Boolean> FILTER = //
            (m, c) -> m.isAnnotationPresent(NotSecured.class) && m.getAnnotation(NotSecured.class).mustBeSecuredByOtherServices();

    @Autowired
    public NotSecuredMethodThatMustBeSecuredByOtherMethodsAdvice(NotSecuredMethodInterceptor interceptor) {
        super(NOT_SECURED_METHOD_THAT_MUST_BE_SECURED_BY_OTHER_METHODS_ORDER, interceptor, FILTER);
    }
}
