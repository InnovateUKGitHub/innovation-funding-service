package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.interceptor.AbstractIfsMethodsAdvice;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.function.BiFunction;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * Advice to be called on methods marked as {@link NotSecured} with {@link NotSecured#mustBeSecuredByOtherServices()}
 * set to true.
 */
@Component
public class NotSecuredMethodThatMustBeSecuredByOtherMethodsAdvice extends AbstractIfsMethodsAdvice {

    public static final int NOT_SECURED_METHOD_THAT_MUST_BE_SECURED_BY_OTHER_METHODS_ORDER = Ordered.LOWEST_PRECEDENCE - 1;

    private static final long serialVersionUID = 1L;

    private static final BiFunction<Method, Class, Boolean> FILTER = //
            (m, c) -> findAnnotation(m, NotSecured.class) != null &&
                      findAnnotation(m, NotSecured.class).mustBeSecuredByOtherServices();

    @Autowired
    public NotSecuredMethodThatMustBeSecuredByOtherMethodsAdvice(NotSecuredMethodInterceptor interceptor) {
        super(NOT_SECURED_METHOD_THAT_MUST_BE_SECURED_BY_OTHER_METHODS_ORDER, interceptor, FILTER);
    }
}
