package com.worth.ifs.profiling;

import com.worth.ifs.cache.RestCacheInvalidationAdvisor;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Component
public class ProfilingAdvisor extends AbstractPointcutAdvisor {

    public static final int PROFILING_ORDER = RestCacheInvalidationAdvisor.REST_CACHE_INVALIDATE - 1;

    public ProfilingAdvisor() {
        setOrder(PROFILING_ORDER);
    }

    private static final long serialVersionUID = 1L;

    @Value("${ifs.web.profilerEnabled:false}")
    private boolean profilerEnabled;

    @Autowired
    Environment environment;

    private final transient StaticMethodMatcherPointcut pointcut = new
            StaticMethodMatcherPointcut() {
                @Override
                public boolean matches(Method method, Class<?> targetClass) {
                    return isProfilerEnabled() && method.isAnnotationPresent(ProfileExecution.class);
                }
            };

    @Autowired
    private transient ProfilingMethodInterceptor interceptor;

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.interceptor;
    }


    public boolean isProfilerEnabled() {
        return profilerEnabled;
    }
}
