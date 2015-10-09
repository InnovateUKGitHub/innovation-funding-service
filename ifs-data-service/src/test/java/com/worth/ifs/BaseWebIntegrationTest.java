package com.worth.ifs;

import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This is the base class for top-level integration tests.  Subclasses of this class will have access to a running embedded Tomcat
 * server on port "23456", as well as full autowiring for test dependencies.  At this level there is no mocking of components taking
 * place.
 *
 * Tests can roll back their changes to the database using the Spring @Rollback annotation.
 *
 * Created by dwatson on 02/10/15.
 */
@WebIntegrationTest("server.port:23456")
@TestPropertySource(locations="classpath:application-web-integration-test.properties")
public abstract class BaseWebIntegrationTest extends BaseIntegrationTest {
}
