package org.innovateuk.ifs.async.executor;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * An implementation of {@link AsyncThreadLocalCopier} that copies Request Attributes from a parent Thread to a child Thread
 */
@Component
public class RequestAttributesAsyncThreadLocalCopier implements AsyncThreadLocalCopier<RequestAttributes> {

    @Override
    public RequestAttributes getOriginalValueFromOriginalThread() {
        return RequestContextHolder.getRequestAttributes();
    }

    @Override
    public void setCopyOfOriginalValueOnAsyncThread(RequestAttributes originalValue) {
        RequestContextHolder.setRequestAttributes(originalValue);
    }

    @Override
    public void clearCopiedValueFromAsyncThread() {
        RequestContextHolder.resetRequestAttributes();
    }
}
