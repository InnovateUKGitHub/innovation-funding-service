package com.worth.ifs;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * A base class for testing services with Spring Security integrated into them.  PermissionRules-annotated beans are
 * made available as mocks so that we can test the effects of calling service methods against the PermissionRule methods
 * that are available.
 * <p>
 * Subclasses of this base class are therefore able to test the security annotations of their various methods by verifying
 * that individual PermissionRule methods are being called (on their owning mocks)
 */
public abstract class BaseServiceSecurityTest<T> extends BaseMockSecurityTest {

    protected T service;

    /**
     * @return the service class under test.  Note that in order for Spring Security to be able to read parameter-name
     * information from expressions like @PreAuthorize("hasPermission(#feedback, 'UPDATE')"), we cannot provide a
     * Mockito mock of type T, as Spring Security is unable to infer which parameter is called "feedback", in this example.
     * <p>
     * Therefore we need to just create a very simple implementation of T.
     */
    protected abstract Class<? extends T> getServiceClass();

    /**
     * Register a temporary bean definition for the class under test (as provided by getServiceClass()), and replace
     * all PermissionRules with mocks that can be looked up with getMockPermissionRulesBean().
     */
    @Before
    public void setup() {

        applicationContext.registerBeanDefinition("beanUndergoingSecurityTesting", new RootBeanDefinition(getServiceClass()));
        service = (T) applicationContext.getBean("beanUndergoingSecurityTesting");

        super.setup();
    }

    @After
    public void teardown() {
        applicationContext.removeBeanDefinition("beanUndergoingSecurityTesting");
        super.teardown();
    }
}
