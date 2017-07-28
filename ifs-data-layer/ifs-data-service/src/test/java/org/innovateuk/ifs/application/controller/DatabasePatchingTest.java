package org.innovateuk.ifs.application.controller;

import org.flywaydb.core.Flyway;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.IfProfileValue;

import static java.util.Arrays.asList;
import static org.junit.Assert.fail;

public class DatabasePatchingTest extends BaseIntegrationTest {

    private static final String[] PATCH_FOLDERS_TO_BE_RUN_ON_PROD = {"db/migration", "db/setup"};

    @Value("${flyway.url}")
    private String databaseUrl;

    @Value("${flyway.user}")
    private String databaseUser;

    @Value("${flyway.password}")
    private String databasePassword;

    @Value("${flyway.locations}")
    private String locations;

    @After
    public void recreateDatabase(){
        cleanAndMigrateDatabaseWithPatches(locations.split("\\s*,\\s*"));
    }

    @Test
    @IfProfileValue(name = "testGroups", values = {"dbpatch"})
    public void testProductionPatches() throws Exception {

        try {
            cleanAndMigrateDatabaseWithPatches(PATCH_FOLDERS_TO_BE_RUN_ON_PROD);
        } catch (Exception e){
            fail("Exception thrown migrating with script directories: " + asList(PATCH_FOLDERS_TO_BE_RUN_ON_PROD) + e.getMessage());
        }
    }

    private void cleanAndMigrateDatabaseWithPatches(String[] patchLocations){
        Flyway f = new Flyway();
        f.setDataSource(databaseUrl, databaseUser, databasePassword);
        f.setLocations(patchLocations);
        f.clean();
        f.migrate();
    }
}
