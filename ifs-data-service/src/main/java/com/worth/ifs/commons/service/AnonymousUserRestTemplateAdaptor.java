package com.worth.ifs.commons.service;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.worth.ifs.commons.security.UidAuthenticationService.AUTH_TOKEN;

/**
 * A Rest Template Adaptor that is able to make restricted calls on behalf of anonymous users e.g. during registration
 */
public class AnonymousUserRestTemplateAdaptor extends AbstractInternalRestTemplateAdaptor {

    private String ifsWebSystemUserUid;

    public AnonymousUserRestTemplateAdaptor(String ifsWebSystemUserUid) {
        this.ifsWebSystemUserUid = ifsWebSystemUserUid;
    }

    @Override
    protected void setAuthenticationToken(HttpHeaders headers) {
        if (SecurityContextHolder.getContext() != null &&
                SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getCredentials() != null) {
            headers.set(AUTH_TOKEN, SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        } else {
            headers.set(AUTH_TOKEN, ifsWebSystemUserUid);
        }
    }
}
