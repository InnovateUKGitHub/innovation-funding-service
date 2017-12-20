package org.innovateuk.ifs.async.config;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests that a {@link SleuthExecutorFactory} is created for the generation of a
 * {@link org.springframework.core.task.TaskExecutor} that supports Sleuth when handling Thread allocation.
 *
 * This allows Sleuth to link parent threads and child threads within its Spans, so that this relationship can be
 * visualised.
 */
@TestPropertySource(properties = "spring.sleuth.enabled=true")
public class AsyncExecutorFactoryWithSleuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AsyncExecutorFactory asyncExecutorFactory;

    @Test
    public void testSleuthAsyncExecutorFactorySelectedWhenSleuthDisabled() {
        assertTrue(SleuthExecutorFactory.class.isAssignableFrom(asyncExecutorFactory.getClass()));
        assertFalse(DefaultExecutorFactory.class.isAssignableFrom(asyncExecutorFactory.getClass()));
    }

}
