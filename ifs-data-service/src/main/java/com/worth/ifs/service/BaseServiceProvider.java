package com.worth.ifs.service;

import org.springframework.beans.factory.annotation.Value;

public class BaseServiceProvider {

    @Value("${ifs.data.service.rest.baseURL}")
    protected String dataRestServiceURL;
}
