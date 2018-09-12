package org.innovateuk.ifs.async.controller;

import org.aopalliance.aop.Advice;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;

/**
 * Places {@link AwaitAsyncFuturesCompletionMethodInterceptor} around {@link Controller} methods to ensure that any
 * Futures generated with {@link AsyncFuturesGenerator} and all of their
 * descendants complete before the Controller method can complete.
 *
 * This advice targets Controller methods that have explicitly been annotated with
 * {@link org.innovateuk.ifs.async.annotations.AsyncMethod}.
 */
@Component
public class AwaitAsyncFuturesCompletionMethodAdvisor extends AbstractPointcutAdvisor {

    private static final long serialVersionUID = 1L;

    public static final int ADVICE_ORDER = Ordered.LOWEST_PRECEDENCE - 1;

    private final transient StaticMethodMatcherPointcut pointcut = new
            StaticMethodMatcherPointcut() {
                @Override
                public boolean matches(Method method, Class<?> targetClass) {
        return targetClass.isAnnotationPresent(Controller.class) &&
                method.isAnnotationPresent(AsyncMethod.class);
        }
    };

    @Autowired
    private transient AwaitAsyncFuturesCompletionMethodInterceptor interceptor;

    public AwaitAsyncFuturesCompletionMethodAdvisor(){
        setOrder(ADVICE_ORDER);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.interceptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AwaitAsyncFuturesCompletionMethodAdvisor that = (AwaitAsyncFuturesCompletionMethodAdvisor) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(pointcut, that.pointcut)
                .append(interceptor, that.interceptor)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(pointcut)
                .append(interceptor)
                .toHashCode();
    }
}
