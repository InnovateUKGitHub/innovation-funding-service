package com.worth.ifs.commons.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Arrays;

/**
 * BaseRestServiceProvider provides a base for all Service classes.
 */

public class BaseRestServiceProvider {

    @Value("${ifs.data.service.rest.baseURL}")
    protected String dataRestServiceURL;

    public static HttpHeaders getJSONHeaders() {
        //set your headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;

    }
}

