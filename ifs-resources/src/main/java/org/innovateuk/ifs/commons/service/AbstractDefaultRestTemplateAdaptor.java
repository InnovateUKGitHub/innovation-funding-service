package org.innovateuk.ifs.commons.service;

import org.innovateuk.ifs.commons.security.authentication.token.Authentication;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
/**
 * The default Rest Template Adaptor that talks to the IFS API, passing the appropriate authentication tokens depending
 * upon who is logged in
 * Note that this does not have the {@link Component} annotation as doing so would force dependent projects to provide
 * the dependencies of this class whether or not it is needed. Instead this class should be subclasses where it is
 * required and the annotation added to that.
 */
public class AbstractDefaultRestTemplateAdaptor extends AbstractInternalRestTemplateAdaptor {

    @Override
    protected void setAuthenticationToken(HttpHeaders headers) {
        if (SecurityContextHolder.getContext() != null &&
                SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getCredentials() != null) {
            headers.set(Authentication.TOKEN, SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        }
    }
}
