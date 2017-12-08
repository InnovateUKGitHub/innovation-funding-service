package org.innovateuk.ifs.async.executor;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * TODO DW - document this class
 */
@Component
public class SpringSecurityAsyncThreadLocalCopier implements AsyncThreadLocalCopier<SecurityContext> {

    @Override
    public SecurityContext getOriginalValueFromOriginalThread() {
        return SecurityContextHolder.getContext();
    }

    @Override
    public void setCopyOfOriginalValueOnAsyncThread(SecurityContext originalValue) {
        SecurityContextHolder.setContext(originalValue);
    }

    @Override
    public void clearCopiedValueFromAsyncThread() {
        SecurityContextHolder.clearContext();
    }
}
