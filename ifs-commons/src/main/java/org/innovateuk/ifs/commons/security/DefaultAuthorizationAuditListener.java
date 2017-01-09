package org.innovateuk.ifs.commons.security;

import org.springframework.boot.actuate.security.AbstractAuthorizationAuditListener;
import org.springframework.security.access.event.AbstractAuthorizationEvent;
import org.springframework.stereotype.Component;

/**
 * Default Authorisation audit listener that does nothing. (replaces AuthorizationAuditListener)
 */
@Component
public class DefaultAuthorizationAuditListener extends AbstractAuthorizationAuditListener {
    @Override
    public void onApplicationEvent(AbstractAuthorizationEvent event) {
        //deliberately empty.
    }
}
