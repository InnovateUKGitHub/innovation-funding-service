package org.innovateuk.ifs.config.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RequestLoggingFilterConfig {

    @Value("${logging.api.inbound.filter.includeQueryString}")
    private Boolean includeQueryString;

    @Value("${logging.api.inbound.filter.includeHeaders}")
    private Boolean includeHeaders;

    @Value("${logging.api.inbound.filter.includePayload}")
    private Boolean includePayload;

    @Value("${logging.api.inbound.filter.maxPayloadLength}")
    private Integer maxPayloadLength;

    @Value("${logging.api.inbound.filter.endpoints}")
    private List<String> loggingEndpoints;

    @Bean
    public InboundAPIRequestLoggingFilter logFilter() {
        InboundAPIRequestLoggingFilter filter
                = new InboundAPIRequestLoggingFilter();
        filter.setIncludeQueryString(includeQueryString);
        filter.setIncludeHeaders(includeHeaders);
        filter.setIncludePayload(includePayload);
        filter.setMaxPayloadLength(maxPayloadLength);
        filter.setLoggingEndpoints(loggingEndpoints);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }
}