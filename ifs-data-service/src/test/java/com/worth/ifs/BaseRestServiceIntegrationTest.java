package com.worth.ifs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${flyway.url}")
    public String databaseUrl;

    @Value("${flyway.user}")
    public String databaseUser;

    @Value("${flyway.password}")
    public String databasePassword;

    @Value("${flyway.locations}")
    public String locations;


    private void cleanAndMigrateDatabaseWithPatches(String[] patchLocations){
        LOG.info("cleanAndMigrateDatabaseWithPatches");

        Flyway f = new Flyway();
        f.setDataSource(databaseUrl, databaseUser, databasePassword);
        f.setLocations(patchLocations);
        f.clean();
        f.migrate();
    }

    /**
     * Need to do a db reset, because spring can't do a @rollback on rest calls...
     */
    @Before
    public void recreateDatabase(){
        cleanAndMigrateDatabaseWithPatches(locations.split("\\s*,\\s*"));
    }
}