package com.worth.ifs.application.controller;

import com.worth.ifs.BaseWebIntegrationTest;
import org.flywaydb.core.Flyway;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.Rollback;

import java.io.File;

import static org.junit.Assert.fail;

@Rollback // TODO? correct
public class DatabasePatchingTest extends BaseWebIntegrationTest {

    @Value("${flyway.url}")
    public String databaseUrl;

    @Value("${flyway.user}")
    public String databaseUser;

    @Value("${flyway.password}")
    public String databasePassword;

    private static final String SCHEMA_SCRIPT_DIRECTORY_NAME = "migration";

    @Test
    public void test() throws Exception {
        File db = new File(Thread.currentThread().getContextClassLoader().getResource("db").toURI());
        for (File dataDirectory : db.listFiles(f -> f.isDirectory() && f.getName() != SCHEMA_SCRIPT_DIRECTORY_NAME)) {
            String[] locations =  new String[]{db.getName() + "/" + SCHEMA_SCRIPT_DIRECTORY_NAME, db.getName() + "/" + dataDirectory.getName()};
            Flyway f = new Flyway();
            f.setDataSource(databaseUrl, databaseUser, databasePassword);
            f.setLocations(locations);
            try {
                f.clean();
                f.migrate();
            } catch (Exception e) {
                fail("Exception thrown migrating with script directories: " + locations);
            }
        }
    }
}
