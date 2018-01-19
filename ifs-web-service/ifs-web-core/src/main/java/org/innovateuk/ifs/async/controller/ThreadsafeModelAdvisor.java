package org.innovateuk.ifs.async.controller;

import org.aopalliance.aop.Advice;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.lang.reflect.Method;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleAnyMatch;

/**
 * An advisor that targets {@link Controller} methods annotated with {@link AsyncMethod} in order to supply them with a threadsafe
 * Spring Model rather than the default non-threadsafe Model.
 */
@Component
public class ThreadsafeModelAdvisor extends AbstractPointcutAdvisor {

    private static final long serialVersionUID = 1L;

    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE;

    private final transient StaticMethodMatcherPointcut pointcut = new
            StaticMethodMatcherPointcut() {
                @Override
                public boolean matches(Method method, Class<?> targetClass) {
        return targetClass.isAnnotationPresent(Controller.class) &&
               method.isAnnotationPresent(AsyncMethod.class) &&
               simpleAnyMatch(method.getParameterTypes(), p -> p.equals(Model.class));
        }
    };

    @Autowired
    private transient ThreadsafeModelMethodInterceptor interceptor;

    public ThreadsafeModelAdvisor(){
        setOrder(ORDER);
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

        ThreadsafeModelAdvisor that = (ThreadsafeModelAdvisor) o;

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
