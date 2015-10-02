package com.worth.ifs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

/**
 * Created by dwatson on 02/10/15.
 */
public abstract class BaseRestServiceIntegrationTest<RestServiceType> extends BaseWebIntegrationTest {

    protected RestServiceType service;

    @Autowired
    protected abstract void setRestService(RestServiceType service);
}