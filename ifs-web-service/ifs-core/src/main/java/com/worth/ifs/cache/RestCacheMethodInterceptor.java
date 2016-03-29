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
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

@Component
public class RestCacheMethodInterceptor implements MethodInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(RestCacheMethodInterceptor.class);
    private final Cache<String, Map<Method, Map<List<Object>, Object>>> cache
            = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();
    @Autowired
    private UidSupplier uidSupplier;

    protected static void put(final Object value, final String uid, final Method method, final List<Object> parameters, final Cache<String, Map<Method, Map<List<Object>, Object>>> cache) throws ExecutionException {
        final Map<Method, Map<List<Object>, Object>> methodMap = cache.get(uid, HashMap::new);
        methodMap.putIfAbsent(method, new HashMap<>());
        final Map<List<Object>, Object> argsMap = methodMap.get(method);
        argsMap.put(parameters, value);
    }

    protected static Optional<Object> get(final String uid, final Method method, final List<Object> parameters, final Cache<String, Map<Method, Map<List<Object>, Object>>> cache) throws ExecutionException {
        final Map<Method, Map<List<Object>, Object>> methodMap = cache.get(uid, HashMap::new);
        final Map<List<Object>, Object> argsMap = methodMap.get(method);
        if (argsMap != null) {
            final Object value = argsMap.get(parameters);
            if (value != null) {
                return Optional.of(value);

            }
        }
        return Optional.empty();
    }

    public UidSupplier getUidSupplier() {
        return uidSupplier;
    }

    public RestCacheMethodInterceptor setUidSupplier(final UidSupplier uidSupplier) {
        this.uidSupplier = uidSupplier;
        return this;
    }

    public void invalidate() {
        final String uid = uidSupplier.get();
        cache.invalidate(uidSupplier.get());
        LOG.debug("Invalidating: " + uid);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // Needs to be a List to use as a key as the equals operation defers to the entries unlike an Array
        final List<Object> parameters = asList(invocation.getArguments());
        final Method method = invocation.getMethod();
        // Get the uid to look up in the cache. Basically a uid for the request.
        final String uid = uidSupplier.get();
        final Optional cached = get(uid, method, parameters, cache);
        if (cached.isPresent()) {
            return cached.get();
        } else {
            final Object toCache = invocation.proceed();
            put(toCache, uid, method, parameters, cache);
            return toCache;
        }
    }
}
