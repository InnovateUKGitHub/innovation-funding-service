package org.innovateuk.ifs.cache;


import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;
import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

/**
 * Provides a unique token for the currently executing HttpServletRequest.  This is used as a key in the cache in
 * {@link RestCacheMethodInterceptor} to ensure that rest cache results are per-Request rather than shared across
 * various requests.
 */
@Component
public class RestCachePerRequestUuidSupplier implements RestCacheUuidSupplier {

    private static final String REQUEST_UUID_KEY = "REQUEST_UUID_KEY";

    @Override
    public String get() {

        if(getRequestAttributes() == null) {
            return UUID.randomUUID().toString();
        }
        if (getRequestAttributes().getAttribute(REQUEST_UUID_KEY, SCOPE_REQUEST) == null) {
            String uuid = UUID.randomUUID().toString();
            setUuid(uuid);
        }
        return (String)getRequestAttributes().getAttribute(REQUEST_UUID_KEY, SCOPE_REQUEST);

    }

    private void setUuid(String uuid) {
        getRequestAttributes().setAttribute(REQUEST_UUID_KEY, uuid, SCOPE_REQUEST);
    }
}
