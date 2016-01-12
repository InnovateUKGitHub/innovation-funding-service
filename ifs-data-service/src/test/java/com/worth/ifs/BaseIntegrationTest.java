package com.worth.ifs;

import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This is the base class for all integration tests against a configured Spring application.  Subclasses of this base can be
 * of the form of either integration tests with a running server ({@link BaseWebIntegrationTest}) or without
 * (e.g. {@link BaseRepositoryIntegrationTest}).
 *
 * Created by dwatson on 02/10/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@TestPropertySource(locations="classpath:application-web-integration-test.properties")
public abstract class BaseIntegrationTest {

    // Boolean to indicate if the test data has been imported yet.
    private static Boolean databaseHasRun = false;

    @Before
    public void before() throws Exception {
        // Initialise the test database with test data if required.
        synchronized (databaseHasRun){
            if (!databaseHasRun) {
                // Get a flyway instance
                Flyway flyway = new Flyway();
                flyway.setDataSource("jdbc:mysql://localhost:3306/ifs_test", "ifs", "ifs");
                // Before running the migrate import the test data.
                ScriptUtils.executeSqlScript(flyway.getDataSource().getConnection(), new EncodedResource(new ClassPathResource("integrationData.sql")), false, false, "--", ";", "##/*", "*/##");
                // Any further migrations.
                flyway.migrate();
                databaseHasRun = true;
            }
        }

    }

}