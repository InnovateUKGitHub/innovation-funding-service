package org.innovateuk.ifs.cache;


import java.util.function.Supplier;

/**
 * Provides a unique token for the currently executing HttpServletRequest.  This is used as a key in the cache in
 * {@link RestCacheMethodInterceptor} to ensure that rest cache results are per-Request rather than shared across
 * various requests.
 */
public interface RestCacheUuidSupplier extends Supplier<String>{}
