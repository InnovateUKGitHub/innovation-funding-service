package com.worth.ifs.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static com.worth.ifs.commons.error.Errors.internalServerErrorError;
import static com.worth.ifs.commons.rest.RestFailures.internalServerErrorRestFailure;
import static com.worth.ifs.commons.rest.RestResult.restFailure;

/**
 *
 */
@Aspect
@Component
public class RestResultExceptionHandlingAdvisor {

    private static final Log LOG = LogFactory.getLog(RestResultExceptionHandlingAdvisor.class);

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) && execution(public com.worth.ifs.commons.rest.RestResult *.*(..))")
    public Object handleReturnedRestResults(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object result = joinPoint.proceed();

            if (result != null) {
                return result;
            } else {
                LOG.warn("Null RestResult returned from method " + joinPoint.getTarget() + " - converting to default 500 RestResult");
                return internalServerErrorRestFailure();
            }
        } catch (Throwable e) {
            LOG.warn("Exception caught while processing RestResult-returning method.  Converting to default 500 RestResult", e);
            return restFailure(internalServerErrorError(e.getMessage()));
        }
    }
}
