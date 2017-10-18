package org.innovateuk.ifs.commons.service;

import org.innovateuk.ifs.commons.security.authentication.token.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * A Rest Template Adaptor that is able to make restricted calls on behalf of anonymous users e.g. during registration
 * Note that this does not have the {@link Component} annotation as doing so would force dependent projects to provide
 * the dependencies of this class whether or not it is needed. Instead this class should be subclasses where it is
 * required and the annotation added to that.
 */
public class AbstractAnonymousUserRestTemplateAdaptor extends AbstractInternalRestTemplateAdaptor {

    @Value("${ifs.web.system.user.uid}")
    private String ifsWebSystemUserUid = null;

    @Override
    protected void setAuthenticationToken(HttpHeaders headers) {
        headers.set(Authentication.TOKEN, ifsWebSystemUserUid);
    }
}
