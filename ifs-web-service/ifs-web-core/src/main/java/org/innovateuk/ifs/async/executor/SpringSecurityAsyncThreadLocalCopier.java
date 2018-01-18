package org.innovateuk.ifs.async.executor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * This {@link AsyncThreadLocalCopier} implementation carries the Spring Security Authentication details from
 * parent Threads to child async Threads so that they can authenticate with the same user as the original spawning Thread.
 */
@Component
public class SpringSecurityAsyncThreadLocalCopier implements AsyncThreadLocalCopier<Authentication> {

    @Override
    public Authentication getOriginalValueFromOriginalThread() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public void setCopyOfOriginalValueOnAsyncThread(Authentication originalValue) {
        SecurityContextHolder.getContext().setAuthentication(originalValue);
    }

    @Override
    public void clearCopiedValueFromAsyncThread() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
