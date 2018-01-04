package org.innovateuk.ifs.async.controller;

import org.innovateuk.ifs.async.generation.AsyncFuturesHolder;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.support.BindingAwareModelMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
    public void testGetMappingsBlockUntilAllGeneratedFuturesComplete() {
        assertFuturesAreWaitedOnBeforeControllerCompletes(futuresCompleted -> controller.getMethod(futuresCompleted));
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

    @Test
    public void testPostMappingsBlock() {
        assertFuturesAreWaitedOnBeforeControllerCompletes(futuresCompleted -> controller.post(futuresCompleted));
    }

    @Test
    public void testPutMappingsDontCurrentlyBlock() {
        assertFuturesNotWaitedOnBeforeControllerCompletes(latch -> controller.put(latch));
    }

    @Test
    public void testDeleteMappingsDontCurrentlyBlock() {
        assertFuturesNotWaitedOnBeforeControllerCompletes(latch -> controller.delete(latch));
    }

    private void assertFuturesAreWaitedOnBeforeControllerCompletes(Consumer<List<String>> methodUnderTest) {

        List<String> futuresCompleted = new ArrayList<>();

        // call the method under test
        methodUnderTest.accept(futuresCompleted);

        // assert that all Futures have completed following the completion of the Controller method
        assertThat(futuresCompleted, containsInAnyOrder("future1", "future2", "awaitingFuture",
                "awaitingFutureChildFuture", "awaitingFutureChildFutureChild"));

        // assert that the list of registered Futures is cleared down from this Thread following
        // the completion of the Controller method
        assertThat(AsyncFuturesHolder.getFuturesOrInitialise(), empty());
    }

    private void assertFuturesNotWaitedOnBeforeControllerCompletes(Consumer<CountDownLatch> methodUnderTest) {

        CountDownLatch latch = new CountDownLatch(1);

        // call the method under test, which should use the latch to create a Future that blocks until the
        // latch is released
        methodUnderTest.accept(latch);

        // and if we've reached this point in the test, the Controller method was NOT blocked by the non-completing
        // Future
        latch.countDown();
    }
}
