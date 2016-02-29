package com.worth.ifs.cache;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestCacheInvalidateMethodInterceptor implements MethodInterceptor {

    @Autowired
    RestCacheMethodInterceptor cacheInterceptor;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        cacheInterceptor.invalidate();
        return invocation.proceed();
    }

    public RestCacheInvalidateMethodInterceptor setCacheInterceptor(RestCacheMethodInterceptor cacheInterceptor) {
        this.cacheInterceptor = cacheInterceptor;
        return this;
    }
}
