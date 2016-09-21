package com.worth.ifs.project;

import com.worth.ifs.BaseIntegrationTest;
import com.worth.ifs.commons.error.exception.ForbiddenActionException;
import com.worth.ifs.commons.service.ExceptionThrowingFunction;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

/**
 * This base class is used to test that the tested controller methods are protected by Aspect-based security.
 *
 * Assuming that the controller method is secured, this test case will enforce that a ForbiddenActionException is thrown
 * when the method is invoked, because the aspect has seen that its advisor advises that the user should not have access.
 *
 * @param <ControllerType>
 * @param <AspectType>
 * @param <AdvisorType>
 */
@DirtiesContext
public abstract class BaseControllerSecurityTest<ControllerType, AspectType, AdvisorType> extends BaseIntegrationTest {

    @Autowired
    private ControllerType controller;

    @Autowired
    private GenericApplicationContext applicationContext;

    private AdvisorType advisorMock;

    @Before
    public void replaceSecurityTesterWithMock() {

        AspectType aspect = applicationContext.getBean(getAspectType());
        advisorMock = mock(getAdvisorType());
        setField(aspect, "advisor", advisorMock);

        controller = applicationContext.getBean(getControllerType());
    }

    protected abstract Class<ControllerType> getControllerType();

    protected abstract Class<AspectType> getAspectType();

    protected abstract Class<AdvisorType> getAdvisorType();

    protected void assertSecured(Consumer<ControllerType> invokeControllerFn) {
        assertForbiddenAction(invokeControllerFn, advisorMock, this::getExpectedSecurityCheck);
    }

    protected abstract boolean getExpectedSecurityCheck(AdvisorType advisor);

    private void assertForbiddenAction(Consumer<ControllerType> invokeControllerFn, AdvisorType advisor, ExceptionThrowingFunction<AdvisorType, Boolean> advisorExpectation) {

        try {
            when(advisorExpectation.apply(advisor)).thenReturn(false);
        } catch (Throwable e) {
            fail("Should not have thrown an exception whilst setting expectations - " + e);
        }

        try {
            invokeControllerFn.accept(controller);
        } catch (ForbiddenActionException e) {
            // expected behaviour
            assertEquals(getExpectedForbiddenMessage(), e.getMessage());
        }

        try {
            advisorExpectation.apply((verify(advisor)));
        } catch (Throwable e) {
            fail("Should not have thrown an exception whilst verifying expectations - " + e);
        }
    }

    protected abstract String getExpectedForbiddenMessage();

}
