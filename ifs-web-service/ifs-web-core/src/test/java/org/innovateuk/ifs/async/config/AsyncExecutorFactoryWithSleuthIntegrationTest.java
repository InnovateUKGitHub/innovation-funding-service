package org.innovateuk.ifs.async.config;

import org.innovateuk.ifs.async.executor.AsyncExecutorFactory;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests that a {@link SleuthExecutorFactory} is created for the generation of a
 * {@link org.springframework.core.task.TaskExecutor} that supports Sleuth when handling Thread allocation.
 *
 * This allows Sleuth to link parent threads and child threads within its Spans, so that this relationship can be
 * visualised.
 *
 * Note that the need for the alternative tomcat.ajp.port and server.port values were necessary to prevent Spring
 * Boot restarting issues when running in collaboration with other tests in this suite (as previously running Spring
 * Boot tests do not appear to be spinning down their embedded containers before this test is launched which, due to
 * this having alternative configuration with "spring.sleuth.enabled=true" available, this test causes a new embedded
 * container to spin up which attempts to use the same ports as other tests' embedded containers.
 */
@TestPropertySource(properties = {"spring.sleuth.enabled=true"})
@DirtiesContext
public class AsyncExecutorFactoryWithSleuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AsyncExecutorFactory asyncExecutorFactory;

    @Test
    public void testSleuthAsyncExecutorFactorySelectedWhenSleuthEnabled() {
        assertTrue(SleuthExecutorFactory.class.isAssignableFrom(asyncExecutorFactory.getClass()));
        assertFalse(DefaultExecutorFactory.class.isAssignableFrom(asyncExecutorFactory.getClass()));
    }

}
