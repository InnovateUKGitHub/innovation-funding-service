package org.innovateuk.ifs.activitylog.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

@Component
public class ActivityLogInterceptor implements MethodInterceptor {
    private static final Log LOG = LogFactory.getLog(ActivityLogInterceptor.class);

    @Autowired
    private ActivityLogService activityLogService;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Activity activity = findAnnotation(invocation.getMethod(), Activity.class);

        if (!StringUtils.isBlank(activity.condition())) {
            if (!handleConditionalStatement(activity, invocation)) {
                return invocation.proceed();
            }
        }

        Object returned = invocation.proceed();

        if (isSuccessfulServiceResult(returned, invocation)) {
            if (!StringUtils.isBlank(activity.applicationId())) {
                Optional<Long> applicationId = findId(activity.applicationId(), invocation);
                applicationId.ifPresent(id -> activityLogService.recordActivityByApplicationId(id, activity.type()));
            } else if (!StringUtils.isBlank(activity.projectId())) {
                Optional<Long> projectId = findId(activity.projectId(), invocation);
                projectId.ifPresent(id -> activityLogService.recordActivityByProjectId(id, activity.type()));
            } else {
                LOG.error(String.format("@Activity annotated method must provide an applicationId or projectId: %s on %s",
                        invocation.getMethod().getName(), invocation.getThis().getClass().getCanonicalName()));
            }
        }

        return returned;
    }

    private Optional<Long> findId(String name, MethodInvocation invocation) {
        Optional<Object> parameter = findParameterByName(invocation, name);

        if (!parameter.isPresent()) {
            LOG.error(String.format("@Activity annotated methods couldn't find identifier parameter: %s on %s",
                    name, invocation.getMethod().getName()));
            return Optional.empty();
        }

        Object identifier = parameter.get();

        if (!(identifier instanceof Long)) {
            LOG.error(String.format("@Activity annotated methods identifier parameter must be a long: %s on %s",
                    name, invocation.getMethod().getName()));
            return Optional.empty();
        }

        return Optional.of((Long) identifier);

    }

    private boolean isSuccessfulServiceResult(Object returned, MethodInvocation invocation) {
        if (returned == null || !(returned instanceof ServiceResult)) {
            LOG.error(String.format("@Activity annotated methods must return not nullable service results: %s on %s",
                    invocation.getMethod().getName(), invocation.getThis().getClass().getCanonicalName()));
            return false;
        } else {
            return ((ServiceResult) returned).isSuccess();
        }
    }

    private boolean handleConditionalStatement(Activity activity, MethodInvocation invocation) {
        String condition = activity.condition();
        Object service = invocation.getThis();

        Method method;
        try {
            method = service.getClass().getMethod(condition, invocation.getMethod().getParameterTypes());
        } catch (NoSuchMethodException e) {
            LOG.error(String.format("@Activity annotated method has no such condition method %s with same params as %s on service %s",
                    condition, invocation.getMethod().getName(), service.getClass().getCanonicalName()),
                    e);
            return false;
        }

        Object result;
        try {
            result = method.invoke(invocation.getThis(), invocation.getArguments());
        } catch (IllegalAccessException
                | InvocationTargetException e) {
            LOG.error(String.format("@Activity annotated method encountered error trying to call condition %s on service %s", condition, service.getClass().getCanonicalName()),
                    e);
            return false;
        }

        if (result == null || !(result instanceof Boolean)) {
            LOG.error(String.format("@Activity annotated method condition didn't return a boolean %s on service %s", condition, service.getClass().getCanonicalName()));
            return false;
        }
        return (Boolean) result;
    }


    private Optional<Object> findParameterByName(MethodInvocation invocation, String name) {
        Optional<Integer> parameterIndex = Optional.empty();

        for (Parameter parameter : invocation.getMethod().getParameters()) {
            if (parameter.getName().equals(name)) {
                parameterIndex = Optional.of(asList(invocation.getMethod().getParameters()).indexOf(parameter));
                break;
            }
        }

        return parameterIndex.map(index -> invocation.getArguments()[index]);
    }
}
