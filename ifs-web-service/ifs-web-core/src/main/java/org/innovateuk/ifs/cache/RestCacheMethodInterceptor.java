package org.innovateuk.ifs.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.innovateuk.ifs.util.ExceptionThrowingRunnable;
import org.innovateuk.ifs.util.ExceptionThrowingSupplier;
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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.Arrays.asList;

@Component
public class RestCacheMethodInterceptor implements MethodInterceptor {

    private static Logger LOG = LoggerFactory.getLogger(RestCacheMethodInterceptor.class);

    private Cache<String, Map<Method, Map<List<Object>, Object>>> cache
            = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();
    
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    @Autowired
    private UidSupplier uidSupplier;

    protected static void put(Object value, String uid, Method method, List<Object> parameters, Cache<String, Map<Method, Map<List<Object>, Object>>> cache, ReadWriteLock lock) throws ExecutionException {

        withWriteLock(lock, () -> {
            Map<Method, Map<List<Object>, Object>> methodMap = cache.get(uid, HashMap::new);
            methodMap.putIfAbsent(method, new HashMap<>());

            Map<List<Object>, Object> argsMap = methodMap.get(method);
            argsMap.put(parameters, value);
        });
    }

    protected static Optional<Object> get(String uid, Method method, List<Object> parameters, Cache<String, Map<Method, Map<List<Object>, Object>>> cache, ReadWriteLock lock) throws ExecutionException {

        return withReadLock(lock, () -> {

            Map<Method, Map<List<Object>, Object>> methodMap = cache.get(uid, HashMap::new);
            Map<List<Object>, Object> argsMap = methodMap.get(method);
            if (argsMap != null) {
                Object value = argsMap.get(parameters);
                if (value != null) {
                    return Optional.of(value);

                }
            }
            return Optional.empty();

        });
    }

    public UidSupplier getUidSupplier() {
        return uidSupplier;
    }

    public RestCacheMethodInterceptor setUidSupplier(UidSupplier uidSupplier) {
        this.uidSupplier = uidSupplier;
        return this;
    }

    public void invalidate() {
        String uid = uidSupplier.get();
        cache.invalidate(uidSupplier.get());
        LOG.debug("Invalidating: " + uid);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // Needs to be a List to use as a key as the equals operation defers to the entries unlike an Array
        List<Object> parameters = asList(invocation.getArguments());
        Method method = invocation.getMethod();
        // Get the uid to look up in the cache. Basically a uid for the request.
        String uid = uidSupplier.get();
        Optional cached = get(uid, method, parameters, cache, lock);
        if (cached.isPresent()) {
            return cached.get();
        } else {
            Object toCache = invocation.proceed();
            put(toCache, uid, method, parameters, cache, lock);
            return toCache;
        }
    }

    private static void withWriteLock(ReadWriteLock lock, ExceptionThrowingRunnable runnable) {
        lock.writeLock().lock();
        try {
            runnable.run();
        } catch (Exception e) {
            LOG.error("Exception found whilst writing to Rest cache", e);
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static <T> T withReadLock(ReadWriteLock lock, ExceptionThrowingSupplier<T> supplier) {
        lock.readLock().lock();
        try {
            return supplier.get();
        } catch (Exception e) {
            LOG.error("Exception found whilst reading to Rest cache", e);
            throw new RuntimeException(e);
        } finally {
            lock.readLock().unlock();
        }
    }
}
