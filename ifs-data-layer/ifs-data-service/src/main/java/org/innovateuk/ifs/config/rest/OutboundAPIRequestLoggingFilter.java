package org.innovateuk.ifs.config.rest;

import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OutboundAPIRequestLoggingFilter extends CommonsRequestLoggingFilter {

    private static final Set<String> OUTBOUND_API_ENDPOINTS = Stream.of(
            "/application-update"
    ).collect(Collectors.toSet());

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return super.shouldLog(request) &&
                OUTBOUND_API_ENDPOINTS.stream().filter(silEndpoint -> request.getServletPath().contains(silEndpoint)).count() > 0;
    }
}
