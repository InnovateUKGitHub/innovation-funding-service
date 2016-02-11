package com.worth.ifs.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;
import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

@Component
public class RestCacheMethodInterceptor implements MethodInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(RestCacheMethodInterceptor.class);
    private static final Cache<String, Map<Method, Map<List<Object>, Object>>> CACHE
            = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();
    private static final String REQUEST_UID_KEY = "REQUEST_UID_KEY";

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // Needs to be a List to use as a key as the equals operation defers to the entries unlike an Array
        final List<Object> parameters = asList(invocation.getArguments());
        final Method method = invocation.getMethod();

        // Generate the Uid for this request if there isn't one
        if (getRequestAttributes().getAttribute(REQUEST_UID_KEY, SCOPE_REQUEST) == null) {
            getRequestAttributes().setAttribute(REQUEST_UID_KEY, UUID.randomUUID().toString(), SCOPE_REQUEST);
        }
        final String requestUid = (String) getRequestAttributes().getAttribute(REQUEST_UID_KEY, SCOPE_REQUEST);

        final Map<Method, Map<List<Object>, Object>> methodMap = CACHE.get(requestUid, HashMap::new);
        methodMap.putIfAbsent(method, new HashMap<>());
        final Map<List<Object>, Object> argsMap = methodMap.get(method);
        if (argsMap.containsKey(parameters)) { // null is an acceptable return argument
            final Object cachedResult = argsMap.get(invocation.getArguments());
            LOG.debug("Returning cached result: " + cachedResult);
            System.out.println("qqRP returning cached");
        }
        final Object result = invocation.proceed();
        argsMap.put(parameters, result);
        CACHE.put(requestUid, methodMap);
        return result;
    }
}
