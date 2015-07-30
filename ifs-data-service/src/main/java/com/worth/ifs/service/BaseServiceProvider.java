package com.worth.ifs.service;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by rogier on 30/07/15.
 */
public class BaseServiceProvider {

    @Value("${ifs.data.service.rest.baseURL}")
    protected String dataRestServiceURL;
}
