package org.innovateuk.ifs.starters.stubdev.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import static org.innovateuk.ifs.starters.stubdev.cfg.StubDevConfigurationProperties.STUB_DEV_PROPS_PREFIX;

/**
 * Conditionally time method calls and prints the output for methods matching the aop selector.
 *
 * The selector matches client calls to the data layer.
 */
@Aspect
@Component
@ConditionalOnProperty(prefix=STUB_DEV_PROPS_PREFIX, name="enableClientMethodTiming", havingValue="true")
public class TimerAspect {

    private static final Logger log = LoggerFactory.getLogger(TimerAspect.class);

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
