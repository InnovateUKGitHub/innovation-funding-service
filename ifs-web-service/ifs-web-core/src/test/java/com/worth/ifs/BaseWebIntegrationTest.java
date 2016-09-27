package com.worth.ifs;

import com.worth.ifs.commons.BaseIntegrationTest;
import org.springframework.boot.test.WebIntegrationTest;

/**
 * This is the base class for top-level integration tests.  Subclasses of this class will have access to a running embedded Tomcat
 * server on a random port, as well as full autowiring for test dependencies.  At this level there is no mocking of components taking
 * place.
 *
 * Created by dwatson on 02/10/15.
 */
@WebIntegrationTest(randomPort = true)
public abstract class BaseWebIntegrationTest extends BaseIntegrationTest {
}
