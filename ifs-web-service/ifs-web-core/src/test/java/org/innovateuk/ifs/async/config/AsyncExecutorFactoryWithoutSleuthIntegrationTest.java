package org.innovateuk.ifs.async.config;

import org.innovateuk.ifs.async.executor.AsyncExecutorFactory;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests that a {@link DefaultExecutorFactory} is created for the generation of a
 * vanilla {@link org.springframework.core.task.TaskExecutor} without the need for Sleuth support
 */
public class AsyncExecutorFactoryWithoutSleuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AsyncExecutorFactory asyncExecutorFactory;

    @Test
    public void testDefaultAsyncExecutorFactorySelectedWhenSleuthDisabled() {
        assertTrue(DefaultExecutorFactory.class.isAssignableFrom(asyncExecutorFactory.getClass()));
        assertFalse(SleuthExecutorFactory.class.isAssignableFrom(asyncExecutorFactory.getClass()));
    }

}
