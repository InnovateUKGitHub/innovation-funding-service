package org.innovateuk.ifs.rest;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;

/**
 * This Advice targets any @RequestMapping Controller methods that return RestResults and ensures that the calling code will receive
 * a RestResult.
 *
 * In effect, if the called method throws an Exception, this'll be transformed into an "Internal Server Error" RestResult, and if
 * it returns null, it'll convert it into an "Internal Server Error" RestResult.
 */
@Aspect
@Slf4j
@Component
public class RestResultExceptionHandlingAdvice {


    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMapping() {
        // does nothing
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getMapping() {
        // does nothing
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping() {
        // does nothing
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMapping() {
        // does nothing
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void deleteMapping() {
        // does nothing
    }

    @Around("(requestMapping() || getMapping() || postMapping() || putMapping() || deleteMapping())" +
            " && execution(public org.innovateuk.ifs.commons.rest.RestResult *.*(..))")
    public Object handleReturnedRestResults(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object result = joinPoint.proceed();

            if (result != null) {
                return result;
            } else {
                log.warn("Null RestResult returned from method " + joinPoint.getTarget() + " - converting to default 500 RestResult");
                return restFailure(internalServerErrorError());
            }
        } catch (Exception e) {
            log.warn("Exception caught while processing RestResult-returning method.  Converting to default 500 RestResult", e);
            return restFailure(internalServerErrorError());
        }
    }
}
