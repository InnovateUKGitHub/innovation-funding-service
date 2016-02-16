package com.worth.ifs.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

import static java.lang.reflect.Modifier.isPublic;

@Component
public class SpringSecurityExceptionHandlingAdvisor extends AbstractPointcutAdvisor {

    private static final long serialVersionUID = 1L;

    public SpringSecurityExceptionHandlingAdvisor(){
        setOrder(LOWEST_PRECEDENCE);
    }

    private final StaticMethodMatcherPointcut pointcut = new
            StaticMethodMatcherPointcut() {
                @Override
                public boolean matches(Method method, Class<?> targetClass) {
                    return targetClass.getAnnotation(Service.class) != null && method.getReturnType().equals(ServiceResult.class) && isPublic(method.getModifiers());
                }
            };

    @Autowired
    private SpringSecurityExceptionHandlingMethodInterceptor interceptor;

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.interceptor;
    }
}
