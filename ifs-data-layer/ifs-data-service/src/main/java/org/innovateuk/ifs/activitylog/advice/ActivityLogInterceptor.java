package org.innovateuk.ifs.activitylog.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
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

    @Autowired
    private ActivityLogService activityLogService;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Activity activity = findAnnotation(invocation.getMethod(), Activity.class);

        ActivityType activityType = activity.type();
        if (!StringUtils.isBlank(activity.dynamicType())) {
            Optional<ActivityType> dynamicType = handleDynamicActivityType(activity, invocation);
            if (!dynamicType.isPresent()) {
                return invocation.proceed();
            }
            activityType = dynamicType.get();
        }

        Object returned = invocation.proceed();

        if (activityType == ActivityType.NONE) {
            throw new IFSRuntimeException(String.format("@Activity annotated method cannot save a NONE ActivityType.%s on %s",
                    invocation.getMethod().getName(), invocation.getThis().getClass().getCanonicalName()));
        }


        if (isSuccessfulServiceResult(returned, invocation)) {
            callAppropriateActivityLogMethod(activity, activityType, invocation);
        }

        return returned;
    }

    private void callAppropriateActivityLogMethod(Activity activity, ActivityType activityType, MethodInvocation invocation) {
        if (!StringUtils.isBlank(activity.applicationId())) {
            Optional<Long> applicationId = findId(activity.applicationId(), invocation, Long.class);
            applicationId.ifPresent(id -> activityLogService.recordActivityByApplicationId(id, activityType));
        } else if (!StringUtils.isBlank(activity.projectId())) {
            Optional<Long> projectId = findId(activity.projectId(), invocation, Long.class);
            projectId.ifPresent(id -> activityLogService.recordActivityByProjectId(id, activityType));
        } else if (!StringUtils.isBlank(activity.projectOrganisationCompositeId())) {
            Optional<ProjectOrganisationCompositeId> projectOrganisationCompositeId = findId(activity.projectOrganisationCompositeId(), invocation, ProjectOrganisationCompositeId.class);
            projectOrganisationCompositeId.ifPresent(id -> activityLogService.recordActivityByProjectIdAndOrganisationId(id.getProjectId(), id.getOrganisationId(), activityType));
        } else {
            throw new IFSRuntimeException(String.format("@Activity annotated method must provide an applicationId or projectId: %s on %s",
                    invocation.getMethod().getName(), invocation.getThis().getClass().getCanonicalName()));
        }
    }

    private <C> Optional<C> findId(String name, MethodInvocation invocation, Class<C> idClass) {
        Optional<Object> parameter = findParameterByName(invocation, name);

        if (!parameter.isPresent()) {
            throw new IFSRuntimeException(String.format("@Activity annotated methods couldn't find identifier parameter: %s on %s",
                    name, invocation.getMethod().getName()));
        }

        Object identifier = parameter.get();

        if (!(idClass.isAssignableFrom(identifier.getClass()))) {
            throw new IFSRuntimeException(String.format("@Activity annotated methods identifier parameter must be of the correct class: %s on %s",
                    name, invocation.getMethod().getName()));
        }

        return Optional.of(idClass.cast(identifier));

    }

    private boolean isSuccessfulServiceResult(Object returned, MethodInvocation invocation) {
        if (!invocation.getMethod().getReturnType().isAssignableFrom(ServiceResult.class)) {
            throw new IFSRuntimeException(String.format("@Activity annotated methods must return not nullable service results: %s on %s",
                    invocation.getMethod().getName(), invocation.getThis().getClass().getCanonicalName()));
        } else {
            return returned != null && ((ServiceResult) returned).isSuccess();
        }
    }

    private Optional<ActivityType> handleDynamicActivityType(Activity activity, MethodInvocation invocation) {
        String dynamicType = activity.dynamicType();
        Object service = invocation.getThis();

        Method method;
        try {
            method = service.getClass().getMethod(dynamicType, invocation.getMethod().getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new IFSRuntimeException(String.format("@Activity annotated method has no such dynamicType method %s with same params as %s on service %s",
                    dynamicType, invocation.getMethod().getName(), service.getClass().getCanonicalName()), e);
        }

        Object result;
        try {
            result = method.invoke(invocation.getThis(), invocation.getArguments());
        } catch (IllegalAccessException
                | InvocationTargetException e) {
            throw new IFSRuntimeException(String.format("@Activity annotated method encountered error trying to call dynamicType %s on service %s", dynamicType, service.getClass().getCanonicalName()),
                    e);
        }

        if (result == null || !(result instanceof Optional)) {
            throw new IFSRuntimeException(String.format("@Activity annotated method dynamicType didn't return a boolean %s on service %s", dynamicType, service.getClass().getCanonicalName()));
        }
        return (Optional<ActivityType>) result;
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
