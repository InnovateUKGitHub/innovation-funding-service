package com.worth.ifs.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import static com.worth.ifs.commons.error.Errors.forbiddenError;
import static com.worth.ifs.commons.error.Errors.internalServerErrorError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;

/**
 *
 */
@Aspect
@Component
public class ServiceFailureTransactionRollbackAdvisor {

    private static final Log LOG = LogFactory.getLog(ServiceFailureTransactionRollbackAdvisor.class);

    @Around("@target(org.springframework.stereotype.Service) && execution(public com.worth.ifs.commons.service.ServiceResult *.*(..))")
    public Object handleReturnedServiceResults(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            ServiceResult<?> result = (ServiceResult<?>) joinPoint.proceed();

            if (result == null || result.isFailure()) {
                handleFailure();
            }

            if (result == null) {
                LOG.warn("Null ServiceResult being returned from method.  Converting to ServiceFailure");
                return serviceFailure(internalServerErrorError("Null ServiceResult returned from method"));
            } else {
                return result;
            }

        } catch (AccessDeniedException | AuthenticationCredentialsNotFoundException e) {
            LOG.warn(e.getClass().getSimpleName() + " caught while processing ServiceResult-returning method.  Converting to a ServiceFailure");
            handleFailure();
            return serviceFailure(forbiddenError(e.getMessage()));
        } catch (Throwable e) {
            LOG.warn(e.getClass().getSimpleName() + "caught while processing ServiceResult-returning method.  Converting to a ServiceFailure");
            handleFailure();
            return serviceFailure(internalServerErrorError(e.getMessage()));
        }
    }

    private void handleFailure() {
        LOG.debug("Failure encountered during processing of a ServiceResult-returning Service method - rolling back any transactions");
        try {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (NoTransactionException e) {
            LOG.trace("No transaction to roll back");
            LOG.trace(e);
        }
    }

}
