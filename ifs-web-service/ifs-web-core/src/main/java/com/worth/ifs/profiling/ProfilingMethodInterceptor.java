package com.worth.ifs.profiling;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public class ProfilingMethodInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ProfilingMethodInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        final StopWatch stopWatch = new StopWatch(invocation.getMethod().toGenericString());
        stopWatch.start( invocation.getMethod().getDeclaringClass() + "." + invocation.getMethod().getName());
        try {
            final Object proceed = invocation.proceed();
            return proceed;

        } finally {
            stopWatch.stop();
            log.info(stopWatch.prettyPrint());
        }
    }
}
