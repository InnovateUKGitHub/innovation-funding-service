package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.interceptor.AbstractIfsMethodsAdvice;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredMethodsInStackCountInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiFunction;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * Advice to be called on methods marked as {@link NotSecured} with {@link NotSecured#mustBeSecuredByOtherServices()}
 * set to true.
 */
@Component
public class SecuredMethodsAdvice extends AbstractIfsMethodsAdvice {

    public static final int SECURED_METHODS_ORDER  = NotSecuredMethodThatMustBeSecuredByOtherMethodsAdvice.NOT_SECURED_METHOD_THAT_MUST_BE_SECURED_BY_OTHER_METHODS_ORDER - 1;

    private static final long serialVersionUID = 1L;

    private static final List<Class<? extends Annotation>> SECURED_ANNOTATIONS = asList(
            PostFilter.class, PreFilter.class, PreAuthorize.class, PostAuthorize.class);

    private static final BiFunction<Method, Class, Boolean> FILTER =
            (m, c) -> !simpleFilter(SECURED_ANNOTATIONS, a -> findAnnotation(m, a) != null).isEmpty();

    @Autowired
    public SecuredMethodsAdvice(final SecuredMethodsInStackCountInterceptor interceptor) {
        super(SECURED_METHODS_ORDER, interceptor, FILTER);
    }
}
