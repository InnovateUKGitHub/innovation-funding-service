package org.innovateuk.ifs.config.rest;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Getter
@Setter
public class OutboundAPIRequestLoggingFilter extends CommonsRequestLoggingFilter {

    private List<String> loggingEndpoints;

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return super.shouldLog(request) &&
                loggingEndpoints.stream().filter(silEndpoint -> request.getServletPath().contains(silEndpoint)).count() > 0;
    }
}
