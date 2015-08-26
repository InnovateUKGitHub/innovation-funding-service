package com.worth.ifs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * BaseServiceProvider provides a base for all Service classes.
 */

public class BaseServiceProvider {

    @Value("${ifs.data.service.rest.baseURL}")
    protected String dataRestServiceURL;


}

