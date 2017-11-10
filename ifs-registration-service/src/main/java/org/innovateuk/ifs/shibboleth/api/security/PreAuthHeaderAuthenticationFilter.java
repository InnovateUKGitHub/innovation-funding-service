package org.innovateuk.ifs.shibboleth.api.security;

import org.innovateuk.ifs.shibboleth.api.ApiProperties;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

class PreAuthHeaderAuthenticationFilter extends RequestHeaderAuthenticationFilter {

    private static final String HEADER = "api-key";

    private final ApiProperties apiProperties;


    public PreAuthHeaderAuthenticationFilter(final ApiProperties apiProperties) {
        super();
        this.apiProperties = apiProperties;
        this.setPrincipalRequestHeader(HEADER);
        this.setExceptionIfHeaderMissing(false);
        this.setAuthenticationDetailsSource(new PreAuthenticationDetailsSource());
    }


    @Override
    protected Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {

        return Optional.ofNullable(
            getApiKey(request)
        ).filter(
            this::validApiKey
        ).orElse(
            null
        );
    }


    @Override
    protected Object getPreAuthenticatedCredentials(final HttpServletRequest request) {
        return getPreAuthenticatedPrincipal(request);
    }


    private String getApiKey(final HttpServletRequest request) {
        return (String) super.getPreAuthenticatedPrincipal(request);
    }


    private boolean validApiKey(final String key) {
        return apiProperties.getKeys().stream().anyMatch(key::equals);
    }
}
