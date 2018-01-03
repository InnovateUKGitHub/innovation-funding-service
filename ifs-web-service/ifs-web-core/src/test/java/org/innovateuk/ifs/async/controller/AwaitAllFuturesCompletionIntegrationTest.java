package org.innovateuk.ifs.async.controller;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * This test tests the Future blocking mechanism that is applied to request-handing Controller methods as applied by
 * {@link AwaitAllFuturesCompletionMethodInterceptor}.  This ensures that all Futures created during the execution of
 * the Controller method have completed before the Controller method finishes.
 */
public class AwaitAllFuturesCompletionIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AwaitAllFuturesCompletionIntegrationTestHelper controller;

    @Test
    public void testGetMethodBlocksUntilAllFuturesComplete() {

        List<String> futuresCompleted = new ArrayList<>();

        // call the method under test
        controller.getMethod(futuresCompleted);

        assertThat(futuresCompleted, containsInAnyOrder("future1", "future2", "awaitingFuture",
                "awaitingFutureChildFuture", "awaitingFutureChildFutureChild"));
    }
}
