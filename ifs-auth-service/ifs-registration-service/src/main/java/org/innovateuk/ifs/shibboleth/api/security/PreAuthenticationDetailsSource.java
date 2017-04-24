package org.innovateuk.ifs.shibboleth.api.security;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

class PreAuthenticationDetailsSource implements
    AuthenticationDetailsSource<HttpServletRequest, PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails> {

    @Override
    public PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails buildDetails(final HttpServletRequest context) {
        return new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(context, new ArrayList<>());
    }

}
