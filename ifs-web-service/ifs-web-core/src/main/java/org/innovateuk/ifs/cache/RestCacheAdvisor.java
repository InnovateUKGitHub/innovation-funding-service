package org.innovateuk.ifs.cache;

import org.aopalliance.aop.Advice;
import org.innovateuk.ifs.async.controller.AwaitModelFuturesCompletionMethodAdvisor;
import org.innovateuk.ifs.commons.service.RestCacheResult;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class RestCacheAdvisor extends AbstractPointcutAdvisor {

    private static final long serialVersionUID = 1L;


    public static final int REST_CACHE = AwaitModelFuturesCompletionMethodAdvisor.ADVICE_ORDER - 1;

    public RestCacheAdvisor(){
        setOrder(REST_CACHE);
    }

    private final transient StaticMethodMatcherPointcut pointcut = new
            StaticMethodMatcherPointcut() {
                @Override
                public boolean matches(Method method, Class<?> targetClass) {
                    return method.isAnnotationPresent(RestCacheResult.class);
                }
            };

    @Autowired
    private transient RestCacheMethodInterceptor interceptor;

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.interceptor;
    }
}
