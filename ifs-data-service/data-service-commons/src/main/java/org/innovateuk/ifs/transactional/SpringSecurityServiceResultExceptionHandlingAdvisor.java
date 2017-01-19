package org.innovateuk.ifs.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

import static java.lang.reflect.Modifier.isPublic;

/**
 * This Advisor places the {@link SpringSecurityServiceResultExceptionHandlingInterceptor} below Spring Security AOP code
 * that is intercepting ServiceResult-returning Service code, and allows it to catch Spring Security exceptions and convert
 * them into failing ServiceResults.
 */
@Component
public class SpringSecurityServiceResultExceptionHandlingAdvisor extends AbstractPointcutAdvisor {

    private static final long serialVersionUID = 1L;

    public SpringSecurityServiceResultExceptionHandlingAdvisor(){
        setOrder(LOWEST_PRECEDENCE);
    }

    private transient final StaticMethodMatcherPointcut pointcut = new
            StaticMethodMatcherPointcut() {
                @Override
                public boolean matches(Method method, Class<?> targetClass) {
                    return targetClass.getAnnotation(Service.class) != null && method.getReturnType().equals(ServiceResult.class) && isPublic(method.getModifiers());
                }
            };

    @Autowired
    private transient SpringSecurityServiceResultExceptionHandlingInterceptor interceptor;

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.interceptor;
    }
}
