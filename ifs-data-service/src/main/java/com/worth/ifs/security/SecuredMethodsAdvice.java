package com.worth.ifs.security;

import com.worth.ifs.commons.interceptor.AbstractIfsMethodsAdvice;
import com.worth.ifs.commons.security.NotSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.function.BiFunction;

import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static java.util.Arrays.asList;

/**
 * Advice to be called on methods marked as {@link NotSecured} with {@link NotSecured#mustBeSecuredByOtherServices()}
 * set to true.
 */
@Component
public class SecuredMethodsAdvice extends AbstractIfsMethodsAdvice {

    public static final int SECURED_METHODS_ORDER  = NotSecuredMethodThatMustBeSecuredByOtherMethodsAdvice.NOT_SECURED_METHOD_THAT_MUST_BE_SECURED_BY_OTHER_METHODS_ORDER - 1;
    private static final long serialVersionUID = 1L;
    private static final BiFunction<Method, Class, Boolean> FILTER =
            (m, c) -> !simpleFilter(asList(PostFilter.class, PreFilter.class, PreAuthorize.class, PostAuthorize.class), a -> m.isAnnotationPresent(a)).isEmpty();

    @Autowired
    public SecuredMethodsAdvice(final SecuredMethodsInStackCountInterceptor interceptor) {
        super(SECURED_METHODS_ORDER, interceptor, FILTER);
    }
}
