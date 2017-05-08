package org.innovateuk.ifs.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.DefaultTransactionStatus;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_SERVICE_RESULT_EXCEPTION_THROWN_DURING_PROCESSING;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_SERVICE_RESULT_NULL_RESULT_RETURNED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;

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
@Aspect
@Component
public class ServiceFailureExceptionHandlingAdvice {

    private static final Log LOG = LogFactory.getLog(ServiceFailureExceptionHandlingAdvice.class);

    /**
     * Track whether or not the current ServiceResult is the top-level one.  If so, it has responsibilities to control
     * transaction rollback based on whether it succeeds or fails
     */
    private ThreadLocal<Boolean> topLevelMethod = new ThreadLocal<>();

    @Around("@target(org.springframework.stereotype.Service) && execution(public org.innovateuk.ifs.commons.service.ServiceResult *.*(..))")
    public Object handleReturnedServiceResults(ProceedingJoinPoint joinPoint) throws Throwable {

        Boolean originalTopLevelValue = topLevelMethod.get();
        boolean currentlyAtTopLevel = topLevelMethod.get() == null;

        try {

            topLevelMethod.set(false);

            ServiceResult<?> result = (ServiceResult<?>) joinPoint.proceed();

            if (result == null || result.isFailure()) {
                handleFailure(result, currentlyAtTopLevel);
            }

            if (result == null) {
                LOG.warn("Null ServiceResult being returned from method.  Converting to ServiceFailure");
                return serviceFailure(GENERAL_SERVICE_RESULT_NULL_RESULT_RETURNED);
            } else {
                return result;
            }

        } catch (Exception e) {
            LOG.warn(e.getClass().getSimpleName() + " caught while processing ServiceResult-returning method.  Converting to a ServiceFailure", e);
            handleFailure(null, currentlyAtTopLevel);
            return serviceFailure(GENERAL_SERVICE_RESULT_EXCEPTION_THROWN_DURING_PROCESSING);
        } finally {
            topLevelMethod.set(originalTopLevelValue);
        }
    }

    private void handleFailure(ServiceResult<?> result, boolean topLevel) {
        LOG.debug("Failure encountered during processing of a ServiceResult-returning Service method");
        if(result!=null) {
            result.getFailure().getErrors().stream().forEach(error ->
                LOG.debug("    " + error.getErrorKey())
            );
        }

        if (topLevel) {
            try {
                if (isWritableTransaction()) {
                    LOG.debug("Failure encountered during processing of a top-level ServiceResult-returning Service method - rolling back any transactions");
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }
            } catch (NoTransactionException e) {
                LOG.trace("No transaction to roll back", e);
            }
        }
    }

    private boolean isWritableTransaction() {
        return !((DefaultTransactionStatus) TransactionAspectSupport.currentTransactionStatus()).isReadOnly();
    }

}
