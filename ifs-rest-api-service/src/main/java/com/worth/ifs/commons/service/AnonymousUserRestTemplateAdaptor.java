package com.worth.ifs.commons.service;

import com.worth.ifs.commons.security.authentication.token.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * A Rest Template Adaptor that is able to make restricted calls on behalf of anonymous users e.g. during registration
 */
@Component
public class AnonymousUserRestTemplateAdaptor extends AbstractInternalRestTemplateAdaptor {

    @Value("${ifs.web.system.user.uid}")
    private String ifsWebSystemUserUid = null;

    @Override
    protected void setAuthenticationToken(HttpHeaders headers) {
        headers.set(Authentication.TOKEN, ifsWebSystemUserUid);
    }
}
