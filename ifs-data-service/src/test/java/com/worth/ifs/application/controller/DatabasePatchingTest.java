package com.worth.ifs.application.controller;

import com.worth.ifs.commons.BaseIntegrationTest;
import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.junit.Assert.fail;

public class DatabasePatchingTest extends BaseIntegrationTest {

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
        ArrayList<String[]> locations = new ArrayList<>();
        locations.add(new String[]{"db/migration"});
        locations.add(new String[]{"db/migration", "db/setup"});
        locations.add(new String[]{"db/migration", "db/setup", "db/webtest"});
        locations.add(new String[]{"db/migration", "db/setup", "db/development"});
        locations.add(new String[]{"db/migration", "db/setup", "db/integration"});

        for (String[] location : locations) {
            try {
                cleanAndMigrateDatabaseWithPatches(location);
            } catch (Exception e){
                fail("Exception thrown migrating with script directories: " + asList(location) + e.getMessage());
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
