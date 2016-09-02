package com.worth.ifs.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import static com.worth.ifs.commons.error.CommonFailureKeys.GENERAL_SERVICE_RESULT_EXCEPTION_THROWN_DURING_PROCESSING;
import static com.worth.ifs.commons.error.CommonFailureKeys.GENERAL_SERVICE_RESULT_NULL_RESULT_RETURNED;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;

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

    @Around("@target(org.springframework.stereotype.Service) && execution(public com.worth.ifs.commons.service.ServiceResult *.*(..))")
    public Object handleReturnedServiceResults(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            ServiceResult<?> result = (ServiceResult<?>) joinPoint.proceed();

            if (result == null || result.isFailure()) {
                handleFailure(result);
            }

            if (result == null) {
                LOG.warn("Null ServiceResult being returned from method.  Converting to ServiceFailure");
                return serviceFailure(GENERAL_SERVICE_RESULT_NULL_RESULT_RETURNED);
            } else {
                return result;
            }

        } catch (Exception e) {
            LOG.warn(e.getClass().getSimpleName() + " caught while processing ServiceResult-returning method.  Converting to a ServiceFailure", e);
            handleFailure(null);
            return serviceFailure(GENERAL_SERVICE_RESULT_EXCEPTION_THROWN_DURING_PROCESSING);
        }
    }

    private void handleFailure(ServiceResult<?> result) {
        LOG.debug("Failure encountered during processing of a ServiceResult-returning Service method - rolling back any transactions");
        if(result!=null) {
            result.getFailure().getErrors().stream().forEach(error ->
                LOG.debug("    " + error.getErrorKey())
            );
        }

        try {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (NoTransactionException e) {
            LOG.trace("No transaction to roll back");
            LOG.trace(e);
        }
    }

}
