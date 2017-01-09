package org.innovateuk.ifs.commons.security;

import org.springframework.boot.actuate.security.AbstractAuthorizationAuditListener;
import org.springframework.security.access.event.AbstractAuthorizationEvent;
import org.springframework.stereotype.Component;

/**
 * Default Authorisation audit listener that does nothing. (replaces AuthorizationAuditListener).
 * We do not need spring to perform any authorisation audit listening for us, and the presence of
 * the default listener was throwing an assertion error when it tried to audit a null users (as we do for our XXXSecurityServiceTests.)
 */
@Component
public class DefaultAuthorizationAuditListener extends AbstractAuthorizationAuditListener {
    @Override
    public void onApplicationEvent(AbstractAuthorizationEvent event) {
        //deliberately empty.
    }
}
