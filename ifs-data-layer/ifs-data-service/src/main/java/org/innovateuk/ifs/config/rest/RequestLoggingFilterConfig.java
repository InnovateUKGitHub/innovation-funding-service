package org.innovateuk.ifs.config.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.List;

@Configuration
public class RequestLoggingFilterConfig {

    @Value("${logging.api.outbound.filter.includeQueryString}")
    private Boolean includeQueryString;

    @Value("${logging.api.outbound.filter.includeHeaders}")
    private Boolean includeHeaders;

    @Value("${logging.api.outbound.filter.includePayload}")
    private Boolean includePayload;

    @Value("${logging.api.outbound.filter.maxPayloadLength}")
    private Integer maxPayloadLength;

    @Value("${logging.api.outbound.filter.endpoints}")
    private List<String> loggingEndpoints;

    @Bean
    public OutboundAPIRequestLoggingFilter logFilter() {
        OutboundAPIRequestLoggingFilter filter
                = new OutboundAPIRequestLoggingFilter();
        filter.setIncludeQueryString(includeQueryString);
        filter.setIncludeHeaders(includeHeaders);
        filter.setIncludePayload(includePayload);
        filter.setMaxPayloadLength(maxPayloadLength);
        filter.setLoggingEndpoints(loggingEndpoints);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }
}