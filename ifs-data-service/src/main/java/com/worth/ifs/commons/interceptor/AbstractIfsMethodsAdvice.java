package com.worth.ifs.commons.interceptor;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;
import java.util.function.BiFunction;

public abstract class AbstractIfsMethodsAdvice extends AbstractPointcutAdvisor {


    private final transient MethodInterceptor interceptor;
    private final transient StaticMethodMatcherPointcut pointcut;

    public AbstractIfsMethodsAdvice(final int order, final MethodInterceptor interceptor, BiFunction<Method, Class, Boolean> filter) {
        setOrder(order);
        this.interceptor = interceptor;
        this.pointcut = new StaticMethodMatcherPointcut() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                return filter.apply(method, targetClass);
            }
        };
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.interceptor;
    }
}
