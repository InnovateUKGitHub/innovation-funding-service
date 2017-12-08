package org.innovateuk.ifs.async.controller;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

/**
 * An advisor that targets web layer Controller handler methods in order to supply them with a threadsafe Spring Model
 * rather than the default non-threadsafe Model.
 */
@Component
public class ThreadsafeModelAdvisor extends AbstractPointcutAdvisor {

    private static final long serialVersionUID = 1L;

    public static final int ORDER = Ordered.HIGHEST_PRECEDENCE;

    public ThreadsafeModelAdvisor(){
        setOrder(ORDER);
    }

    private final transient StaticMethodMatcherPointcut pointcut = new
            StaticMethodMatcherPointcut() {
                @Override
                public boolean matches(Method method, Class<?> targetClass) {
                    return method.isAnnotationPresent(RequestMapping.class)
                            || method.isAnnotationPresent(GetMapping.class)
                            || method.isAnnotationPresent(PostMapping.class);
                }
            };

    @Autowired
    private transient ThreadsafeModelMethodInterceptor interceptor;

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.interceptor;
    }
}
