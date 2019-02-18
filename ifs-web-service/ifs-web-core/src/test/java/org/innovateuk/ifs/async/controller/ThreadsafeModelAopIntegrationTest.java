package org.innovateuk.ifs.async.controller;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.function.Consumer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

/**
 * This test suite tests the correct swapping out of the bog-standard Spring Model for a threadsafe wrapper class,
 * {@link ThreadsafeModel}.
 *
 * We expect @GetMapping- and @PostMapping-annotated methods to be candidates for ThreadsafeModel injection, with other
 * methods ignored.
 */
public class ThreadsafeModelAopIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ThreadsafeModelAopIntegrationTestHelper controller;

    private Consumer<Model> modelConsumerMock;

    @Before
    public void setConsumer() {
        modelConsumerMock = mock(Consumer.class);
        controller.setModelConsumer(modelConsumerMock);
    }

    /**
     * Test that a Model can be swapped out for a Threadsafe model on a @GetMapping method that requires a Model method
     * parameter.
     */
    @Test
    public void testModelSwappedOutOnAsyncAnnotatedMethod() {
        assertModelSwappedOutForThreadsafeModel(controller::get);
    }

    /**
     * Test that a Model can be swapped out for a Threadsafe model on a @PostMapping method that requires a Model method
     * parameter.
     */
    @Test
    public void testModelNotSwappedOutOnNonAsyncAnnotatedMethod() {
        assertModelNotSwappedOut(controller::post);
    }

    /**
     * Test that a Model can be swapped out for a Threadsafe model on a @GetMapping method that requires a Model method
     * parameter in an arbitrary position in the method parameter order.
     */
    @Test
    public void testModelSwappedOutOnGetMethodArbitraryParameterIndex() {
        assertModelSwappedOutForThreadsafeModel(model -> controller.getWithOtherParameters(0, 1, model, 3));
    }

    /**
     * Test that a Model cannot currently be swapped out for a Threadsafe model on a @GetMapping method that requires an
     * ExtendedModelMap.
     *
     * This is because we don't currently have a Threadsafe wrapper that subclasses ExtendedModelMap (but we could create
     * one for the future)
     */
    @Test
    public void testModelNotSwappedOutWithExtendedModelMap() {
        assertModelNotSwappedOut(new ExtendedModelMap(), model -> controller.getWithExtendedModelMap(0, 1, model, 3));
    }

    /**
     * Test that a Model cannot currently be swapped out for a Threadsafe model on a @GetMapping method that requires a
     * RedirectAttributes.
     *
     * This is because we don't currently have a Threadsafe wrapper that subclasses RedirectAttributes (but we could
     * create one for the future)
     */
    @Test
    public void testModelNotSwappedOutWithRedirectAttributes() {
        assertModelNotSwappedOut(new RedirectAttributesModelMap(), model -> controller.getWithRedirectAttributes(0, 1, model, 3));
    }

    private void assertModelSwappedOutForThreadsafeModel(Consumer<Model> controllerCall) {
        assertModelSwappedOutForThreadsafeModel(new BindingAwareModelMap(), controllerCall);
    }

    private <T extends Model> void assertModelSwappedOutForThreadsafeModel(T normalModel, Consumer<T> controllerCall) {

        doAnswer(invocation -> {

            ThreadsafeModel threadsafeModel = (ThreadsafeModel) invocation.getArguments()[0];
            assertNotNull(threadsafeModel);
            Model originalModel = (Model) ReflectionTestUtils.getField(threadsafeModel, "model");
            assertSame(normalModel, originalModel);
            return null;

        }).when(modelConsumerMock).accept(isA(ThreadsafeModel.class));

        controllerCall.accept(normalModel);

        verify(modelConsumerMock).accept(isA(ThreadsafeModel.class));
    }

    private void assertModelNotSwappedOut(Consumer<Model> controllerCall) {
        assertModelNotSwappedOut(new BindingAwareModelMap(), controllerCall);
    }

    private <T extends Model> void assertModelNotSwappedOut(T normalModel, Consumer<T> controllerCall) {

        doAnswer(invocation -> {

            Model model = (Model) invocation.getArguments()[0];
            assertNotNull(model);
            assertSame(normalModel, model);
            return null;

        }).when(modelConsumerMock).accept(isA(Model.class));

        controllerCall.accept(normalModel);

        verify(modelConsumerMock).accept(isA(Model.class));
    }
}
