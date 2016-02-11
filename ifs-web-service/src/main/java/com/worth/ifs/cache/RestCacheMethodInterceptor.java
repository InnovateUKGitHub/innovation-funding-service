package com.worth.ifs.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

@Component
public class RestCacheMethodInterceptor implements MethodInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(RestCacheMethodInterceptor.class);
    private static final Cache<String, Map<Method, Map<List<Object>, Object>>> CACHE
            = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();


    @Autowired
    private UidSupplier uidSupplier;

    public void invalidate(){
        final String uid = uidSupplier.get();
        CACHE.invalidate(uidSupplier.get());
        LOG.debug("Invalidating: " + uid);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // Needs to be a List to use as a key as the equals operation defers to the entries unlike an Array
        final List<Object> parameters = asList(invocation.getArguments());
        final Method method = invocation.getMethod();

        // Generate the Uid for this request if there isn't one
        String uid = uidSupplier.get();
        final Map<Method, Map<List<Object>, Object>> methodMap = CACHE.get(uid, HashMap::new);
        methodMap.putIfAbsent(method, new HashMap<>());
        final Map<List<Object>, Object> argsMap = methodMap.get(method);
        if (argsMap.containsKey(parameters)) { // null is an acceptable return argument
            final Object cachedResult = argsMap.get(parameters);
            LOG.debug("Returning cached result: " + cachedResult);
            return cachedResult;
        }
        final Object result = invocation.proceed();
        argsMap.put(parameters, result);
        CACHE.put(uid, methodMap);
        return result;
    }


}
