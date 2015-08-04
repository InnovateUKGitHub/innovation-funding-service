package com.worth.ifs.service;

import org.springframework.beans.factory.annotation.Value;

/**
 * BaseServiceProvider provides a base for all Service classes.
 */

public class BaseServiceProvider {

    @Value("${ifs.data.service.rest.baseURL}")
    protected String dataRestServiceURL;
}
