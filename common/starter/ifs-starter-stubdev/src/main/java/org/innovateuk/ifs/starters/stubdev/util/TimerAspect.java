package org.innovateuk.ifs.starters.stubdev.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;

import static org.innovateuk.ifs.starters.stubdev.Constants.STUB_DEV_PROPS_PREFIX;

@Aspect
@Slf4j
@Component
@ConditionalOnProperty(prefix=STUB_DEV_PROPS_PREFIX, name="enableClientMethodTiming", havingValue="true")
public class TimerAspect {

    @Around("execution(* org.innovateuk.ifs..service.*.*(..))")
    public Object methodTimeLogger(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        StopWatch stopWatch = new StopWatch(methodSignature.getDeclaringType().getSimpleName());
        stopWatch.start(methodSignature.getName());
        Object result = proceedingJoinPoint.proceed();
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        return result;
    }
}
