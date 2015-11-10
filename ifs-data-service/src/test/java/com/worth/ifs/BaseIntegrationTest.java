package com.worth.ifs;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.SpringApplicationConfiguration;
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
}