package org.innovateuk.ifs.async.controller;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import java.util.function.Consumer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.isA;
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

    @Test
    public void testModelSwappedOutOnGetMethod() {
        assertModelSwappedOutForThreadsafeModel(model -> controller.get(model));
    }

    @Test
    public void testModelSwappedOutOnPostMethod() {
        assertModelSwappedOutForThreadsafeModel(model -> controller.post(model));
    }

    @Test
    public void testModelNotSwappedOutOnPutMethod() {
        assertModelNotSwappedOut(model -> controller.put(model));
    }

    @Test
    public void testModelNotSwappedOutOnDeleteMethod() {
        assertModelNotSwappedOut(model -> controller.delete(model));
    }

    @Test
    public void testModelSwappedOutOnGetMethodArbitraryParameterIndex() {
        assertModelSwappedOutForThreadsafeModel(model -> controller.getWithOtherParameters(0, 1, model, 3));
    }

    @Test
    public void testModelNotSwappedOutOnNonRequestHandlingMethod() {
        assertModelNotSwappedOut(model -> controller.nonRequestHandling(model));
    }

    private void assertModelSwappedOutForThreadsafeModel(Consumer<Model> controllerCall) {

        BindingAwareModelMap normalModel = new BindingAwareModelMap();

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

        BindingAwareModelMap normalModel = new BindingAwareModelMap();

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
