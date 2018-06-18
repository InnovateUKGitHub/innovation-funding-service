package org.innovateuk.ifs.async.executor;

import org.innovateuk.ifs.async.util.AsyncAllowedThreadLocal;
import org.springframework.stereotype.Component;

/**
 * This {@link AsyncThreadLocalCopier} implementation is responsible for copying across whether or not we're
 * currently allowed to invoke async code using {@link org.innovateuk.ifs.async.generation.AsyncFuturesGenerator}.
 *
 * This is a safety measure to only allow async execution if the Thread from the Executor was definitely
 * generated via our async mechanism.
 */
@Component
public class AsyncAllowedCopier implements AsyncThreadLocalCopier<Boolean> {

    @Override
    public Boolean getOriginalValueFromOriginalThread() {
        return AsyncAllowedThreadLocal.isAsyncAllowed();
    }

    @Override
    public void clearCopiedValueFromAsyncThread() {
        AsyncAllowedThreadLocal.clearAsyncAllowed();
    }

    @Override
    public void setCopyOfOriginalValueOnAsyncThread(Boolean originalValue) {
        AsyncAllowedThreadLocal.setAsyncAllowed(originalValue);
    }
}
