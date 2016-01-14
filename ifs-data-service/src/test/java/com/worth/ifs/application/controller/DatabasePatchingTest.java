package com.worth.ifs.application.controller;

import com.worth.ifs.BaseWebIntegrationTest;
import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

import static java.util.Arrays.asList;
import static org.junit.Assert.fail;

public class DatabasePatchingTest extends BaseWebIntegrationTest {

    @Value("${flyway.url}")
    public String databaseUrl;

    @Value("${flyway.user}")
    public String databaseUser;

    @Value("${flyway.password}")
    public String databasePassword;

    @Value("${flyway.locations}")
    public String locations;

    private static final String SCHEMA_SCRIPT_DIRECTORY_NAME = "migration";

    @Test
    public void test() throws Exception {
        File db = new File(Thread.currentThread().getContextClassLoader().getResource("db").toURI());
        for (File dataDirectory : db.listFiles(f -> f.isDirectory() && f.getName() != SCHEMA_SCRIPT_DIRECTORY_NAME)) {
            String[] locations =  new String[]{db.getName() + "/" + SCHEMA_SCRIPT_DIRECTORY_NAME, db.getName() + "/" + dataDirectory.getName()};
            try {
                cleanAndMigrateDatabaseWithPatches(locations);
            } catch (Exception e){
                fail("Exception thrown migrating with script directories: " + asList(locations) + e.getMessage());
            }
        }
    }

    private void cleanAndMigrateDatabaseWithPatches(String[] patchLocations){
        Flyway f = new Flyway();
        f.setDataSource(databaseUrl, databaseUser, databasePassword);
        f.setLocations(patchLocations);
        f.clean();
        f.migrate();
    }

    @After
    public void recreateDatabase(){
        cleanAndMigrateDatabaseWithPatches(locations.split("\\s*,\\s*"));
    }


}
