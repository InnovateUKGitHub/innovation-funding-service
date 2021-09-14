package org.innovateuk.ifs;

import org.mockito.InjectMocks;

/**
 * This is the base class for testing Services with mock components.
 *
 */
public abstract class BaseServiceUnitTest<ServiceType> extends BaseUnitTest {

    @InjectMocks
    protected ServiceType service = supplyServiceUnderTest();

    protected abstract ServiceType supplyServiceUnderTest();
}
