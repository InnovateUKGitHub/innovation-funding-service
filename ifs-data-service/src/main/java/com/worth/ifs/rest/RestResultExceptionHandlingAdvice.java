package com.worth.ifs.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;

/**
 * This Advice targets any @RequestMapping Controller methods that return RestResults and ensures that the calling code will receive
 * a RestResult.
 *
 * In effect, if the called method throws an Exception, this'll be transformed into an "Internal Server Error" RestResult, and if
 * it returns null, it'll convert it into an "Internal Server Error" RestResult.
 */
@Aspect
@Component
public class RestResultExceptionHandlingAdvice {

    private static final Log LOG = LogFactory.getLog(RestResultExceptionHandlingAdvice.class);

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) && execution(public com.worth.ifs.commons.rest.RestResult *.*(..))")
    public Object handleReturnedRestResults(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object result = joinPoint.proceed();

            if (result != null) {
                return result;
            } else {
                LOG.warn("Null RestResult returned from method " + joinPoint.getTarget() + " - converting to default 500 RestResult");
                return restFailure(internalServerErrorError());
            }
        } catch (Exception e) {
            LOG.warn("Exception caught while processing RestResult-returning method.  Converting to default 500 RestResult", e);
            return restFailure(internalServerErrorError());
        }
    }
}
