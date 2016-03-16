package com.worth.ifs.cache;

import com.worth.ifs.commons.service.RestCacheInvalidateResult;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class RestCacheInvalidationAdvisor extends AbstractPointcutAdvisor {

    private static final long serialVersionUID = 1L;


    public static final int REST_CACHE_INVALIDATE = RestCacheAdvisor.REST_CACHE - 1;

    public RestCacheInvalidationAdvisor(){
        setOrder(REST_CACHE_INVALIDATE);
    }

    private final transient StaticMethodMatcherPointcut pointcut = new
            StaticMethodMatcherPointcut() {
                @Override
                public boolean matches(Method method, Class<?> targetClass) {
                    return method.isAnnotationPresent(RestCacheInvalidateResult.class);
                }
            };

    @Autowired
    private transient RestCacheInvalidateMethodInterceptor interceptor;

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.interceptor;
    }
}
