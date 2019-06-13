package org.innovateuk.ifs.activitylog.advice;

import org.innovateuk.ifs.commons.interceptor.AbstractIfsMethodsAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.function.BiFunction;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

@Component
public class ActivityLogAdvice extends AbstractIfsMethodsAdvice {

    private static final BiFunction<Method, Class, Boolean> FILTER =
            (m, c) ->
                    findAnnotation(m, Activity.class) != null;

    @Autowired
    public ActivityLogAdvice(ActivityLogInterceptor interceptor) {
        super(Ordered.LOWEST_PRECEDENCE - 5, interceptor, FILTER);
    }
}
