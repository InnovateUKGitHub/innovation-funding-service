package org.innovateuk.ifs.async.controller;

import org.aopalliance.aop.Advice;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

/**
 * Places {@link AwaitModelFuturesCompletionMethodInterceptor} around {@link Controller} methods to ensure that any
 * Futures added directly to the Spring Model as a Future are resolved before the Controller is allowed to complete.
 */
@Component
public class AwaitModelFuturesCompletionMethodAdvisor extends AbstractPointcutAdvisor {

    private static final long serialVersionUID = 1L;

    public static final int ADVICE_ORDER =
            AwaitAsyncFuturesCompletionMethodAdvisor.ADVICE_ORDER - 1;

    private final transient StaticMethodMatcherPointcut pointcut = new
            StaticMethodMatcherPointcut() {
                @Override
                public boolean matches(Method method, Class<?> targetClass) {
        return targetClass.isAnnotationPresent(Controller.class) &&
                (  method.isAnnotationPresent(RequestMapping.class)
                || method.isAnnotationPresent(GetMapping.class)
                || method.isAnnotationPresent(PostMapping.class));
        }
    };

    @Autowired
    private transient AwaitModelFuturesCompletionMethodInterceptor interceptor;

    public AwaitModelFuturesCompletionMethodAdvisor(){
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

        AwaitModelFuturesCompletionMethodAdvisor that = (AwaitModelFuturesCompletionMethodAdvisor) o;

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
