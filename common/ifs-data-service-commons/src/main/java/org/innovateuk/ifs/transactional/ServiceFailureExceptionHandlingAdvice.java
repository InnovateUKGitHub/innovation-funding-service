package org.innovateuk.ifs.transactional;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.DefaultTransactionStatus;

import java.lang.reflect.Method;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_SERVICE_RESULT_EXCEPTION_THROWN_DURING_PROCESSING;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_SERVICE_RESULT_NULL_RESULT_RETURNED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * This Advice targets public Service methods that return ServiceResults and ensure that the calling code will receive a ServiceResult.
 *
 * It also ensures that any Transaction in progress will be rolled back if a failing ServiceResult or an Exception is encountered.
 *
 * In effect, if the called method throws an Exception, this'll be transformed into an "Internal Server Error" ServiceResult, and if
 * it returns null, it'll convert it into an "Internal Server Error" ServiceResult.
 *
 * Transactions will be rolled back for null results, failing ServiceResults, or exceptions being thrown.
 *
 * This Aspect is attached between the TransactionManager and the code being called.
 */
@Slf4j
@Aspect
@Component
public class ServiceFailureExceptionHandlingAdvice {

    /**
     * Track whether or not the current ServiceResult is the top-level one.  If so, it has responsibilities to control
     * transaction rollback based on whether it succeeds or fails
     */
    private ThreadLocal<Boolean> topLevelTransactionalMethod = new ThreadLocal<>();

    @Around("@target(org.springframework.stereotype.Service) && execution(public org.innovateuk.ifs.commons.service.ServiceResult *.*(..))")
    public Object handleReturnedServiceResults(ProceedingJoinPoint joinPoint) throws Throwable {

        Boolean originalTopLevelValue = topLevelTransactionalMethod.get();
        boolean currentlyAtTopLevel = topLevelTransactionalMethod.get() == null;

        try {

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            if (findAnnotation(method, Transactional.class) != null || findAnnotation(method.getDeclaringClass(), Transactional.class) != null) {
                topLevelTransactionalMethod.set(false);
            }

            ServiceResult<?> result = (ServiceResult<?>) joinPoint.proceed();

            if (result == null || result.isFailure()) {
                handleFailure(result, currentlyAtTopLevel);
            }

            if (result == null) {
                log.warn("Null ServiceResult being returned from method.  Converting to ServiceFailure");
                return serviceFailure(GENERAL_SERVICE_RESULT_NULL_RESULT_RETURNED);
            } else {
                return result;
            }

        } catch (Exception e) {
            log.warn(e.getClass().getSimpleName() + " caught while processing ServiceResult-returning method.  Converting to a ServiceFailure", e);
            handleFailure(null, currentlyAtTopLevel);
            return serviceFailure(GENERAL_SERVICE_RESULT_EXCEPTION_THROWN_DURING_PROCESSING);
        } finally {
            topLevelTransactionalMethod.set(originalTopLevelValue);
        }
    }

    private void handleFailure(ServiceResult<?> result, boolean topLevel) {
        log.debug("Failure encountered during processing of a ServiceResult-returning Service method");
        if(result!=null) {
            result.getFailure().getErrors().stream().forEach(error ->
                log.debug("    " + error.getErrorKey())
            );
        }

        if (topLevel) {
            try {
                if (isWritableTransaction()) {
                    log.debug("Failure encountered during processing of a top-level ServiceResult-returning Service method - rolling back any transactions");
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }
            } catch (NoTransactionException e) {
                log.trace("No transaction to roll back", e);
            }
        }
    }

    private boolean isWritableTransaction() {
        return !((DefaultTransactionStatus) TransactionAspectSupport.currentTransactionStatus()).isReadOnly();
    }

}
