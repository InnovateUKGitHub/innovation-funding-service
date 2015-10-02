package com.worth.ifs;

import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by dwatson on 02/10/15.
 */
@WebIntegrationTest("server.port:23456")
@TestPropertySource(locations="classpath:application-web-integration-test.properties")
public abstract class BaseWebIntegrationTest extends BaseIntegrationTest {
}
