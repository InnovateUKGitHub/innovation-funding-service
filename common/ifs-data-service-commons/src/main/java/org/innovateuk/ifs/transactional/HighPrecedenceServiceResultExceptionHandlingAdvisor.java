package org.innovateuk.ifs.transactional;

import org.aopalliance.aop.Advice;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

import static java.lang.reflect.Modifier.isPublic;

/**
 * This Advisor places the {@link HighPrecedenceServiceResultExceptionHandlingInterceptor} above Spring Security AOP code
 * that is intercepting ServiceResult-returning Service code, and allows it to catch Spring Exceptions and convert
 * them into failing ServiceResults.
 */
@Component
public class HighPrecedenceServiceResultExceptionHandlingAdvisor extends AbstractPointcutAdvisor {

    private static final long serialVersionUID = 1L;

    public HighPrecedenceServiceResultExceptionHandlingAdvisor(){
        setOrder(HIGHEST_PRECEDENCE);
    }

    private transient final StaticMethodMatcherPointcut pointcut = new
            StaticMethodMatcherPointcut() {
                @Override
                public boolean matches(Method method, Class<?> targetClass) {
                    return targetClass.getAnnotation(Service.class) != null && method.getReturnType().equals(ServiceResult.class) && isPublic(method.getModifiers());
                }
            };

    @Autowired
    private transient HighPrecedenceServiceResultExceptionHandlingInterceptor interceptor;

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.interceptor;
    }
}
