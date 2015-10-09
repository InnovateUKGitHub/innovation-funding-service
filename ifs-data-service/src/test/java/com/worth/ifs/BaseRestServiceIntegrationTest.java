package com.worth.ifs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

/**
 * This is the base class for testing REST services with full integration with a running "data" layer server on
 * port "23456" and its underlying database.
 *
 * Tests can rollback their changes to the database by using the @Rollback annotation.
 *
 * Created by dwatson on 02/10/15.
 */
public abstract class BaseRestServiceIntegrationTest<RestServiceType> extends BaseWebIntegrationTest {

    protected RestServiceType service;

    @Autowired
    protected abstract void setRestService(RestServiceType service);
}