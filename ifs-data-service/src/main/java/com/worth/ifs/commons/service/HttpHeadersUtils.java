package com.worth.ifs.commons.service;

import org.springframework.http.HttpHeaders;

import static java.util.Collections.singletonList;
import static org.springframework.http.MediaType.APPLICATION_JSON;


public final class HttpHeadersUtils {
    public static HttpHeaders getJSONHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        headers.setAccept(singletonList(APPLICATION_JSON));
        return headers;
    }

}
