package org.innovateuk.ifs.commons;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This is the base class for top-level integration tests.  Subclasses of this class will have access to a running embedded Tomcat
 * server on a random free port (@LocalServerPort), as well as full autowiring for test dependencies.  At this level there is no mocking of components taking
 * place.
 *
 * Tests can roll back their changes to the database using the Spring @Rollback annotation.
 *
 * Created by dwatson on 02/10/15.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseWebIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort
    protected int port;

}
