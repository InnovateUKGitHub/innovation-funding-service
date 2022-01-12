package org.innovateuk.ifs.transactional;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;

/**
 * This Interceptor operates with the highest precedence and intercepts ServiceResult-returning service methods
 * see {@link HighPrecedenceServiceResultExceptionHandlingAdvisor}. As such it will be executed first and so can catch
 * all {@link Exception}s thrown by Spring code before the target method is called or after it has returned.
 * Exceptions thrown from withing the target method are dealt with lower in the stack by
 * {@Link ServiceFailureExceptionHandlingAdvice}. In practice this means Spring permission and transaction Exceptions.
 */
@Slf4j
@Component
public class HighPrecedenceServiceResultExceptionHandlingInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } catch (AccessDeniedException e) {
            log.warn("Authentication denied calling method [" + "class:" + invocation.getThis().getClass().getName() + " method:" + invocation.getMethod().getName() +  "]");
            return serviceFailure(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION);
        } catch (AuthenticationCredentialsNotFoundException e) {
            log.error("AuthenticationCredentialsNotFoundException caught while processing ServiceResult-returning method.  Converting to a ServiceFailure", e);
            return serviceFailure(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Optimistic locking failure calling method [" + "class:" + invocation.getThis().getClass().getName() + " method:" + invocation.getMethod().getName() +  "]");
            return serviceFailure(GENERAL_OPTIMISTIC_LOCKING_FAILURE);
        }
        catch (Exception e) {
            log.error(e.getClass().getSimpleName() + " caught while processing ServiceResult-returning method.  Converting to a 500 ServiceFailure", e);
            return serviceFailure(GENERAL_SPRING_SECURITY_OTHER_EXCEPTION_THROWN);
        }
    }
}
