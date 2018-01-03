package org.innovateuk.ifs.async.controller;

import org.innovateuk.ifs.async.generation.AsyncFuturesHolder;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.support.BindingAwareModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

/**
 * This test tests the Future blocking mechanism that is applied to request-handing Controller methods as applied by
 * {@link AwaitAllFuturesCompletionMethodInterceptor}.  This ensures that all Futures created during the execution of
 * the Controller method have completed before the Controller method finishes.
 */
public class AwaitAllFuturesCompletionIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AwaitAllFuturesCompletionIntegrationTestHelper controller;

    /**
     * This method asserts that Futures created using the
     * {@link org.innovateuk.ifs.async.generation.AsyncFuturesGenerator#async} and
     * {@link org.innovateuk.ifs.async.generation.AsyncFuturesGenerator#awaitAll} methods will block the
     * Controller from completing until they themselves have completed.
     */
    @Test
    public void testGetMethodBlocksUntilAllGeneratedFuturesComplete() {

        List<String> futuresCompleted = new ArrayList<>();

        // call the method under test
        controller.getMethod(futuresCompleted);

        // assert that all Futures have completed following the completion of the Controller method
        assertThat(futuresCompleted, containsInAnyOrder("future1", "future2", "awaitingFuture",
                "awaitingFutureChildFuture", "awaitingFutureChildFutureChild"));

        // assert that the list of registered Futures is cleared down from this Thread following
        // the completion of the Controller method
        assertThat(AsyncFuturesHolder.getFuturesOrInitialise(), empty());
    }

    @Test
    public void testGetMethodBlocksUntilAllModelFuturesComplete() {

        BindingAwareModelMap model = new BindingAwareModelMap();

        // call the method under test
        controller.getMethodWithFutureAddedToModel(model);

        // the Future result should be available on the Model immediately after the Controller
        // call finishes, despite its delay
        assertThat(model.get("futureResult"), equalTo("theResult"));
    }
}
