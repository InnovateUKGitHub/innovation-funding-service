package com.worth.ifs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This is the base class for testing REST services with full integration with a running "data" layer server on
 * port "23456" and its underlying database.
 *
 * Tests can rollback their changes to the database by using the @Rollback annotation.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class BaseRestServiceIntegrationTest<RestServiceType> extends BaseWebIntegrationTest {
    private static final Log LOG = LogFactory.getLog(BaseRestServiceIntegrationTest.class);
    protected RestServiceType service;

    @Autowired
    protected abstract void setRestService(RestServiceType service);
}