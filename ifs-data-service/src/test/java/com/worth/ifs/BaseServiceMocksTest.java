package com.worth.ifs;

import org.mockito.InjectMocks;

/**
 * This is the base class for testing Services with mock components.
 *
 */
public abstract class BaseServiceMocksTest<ServiceType> extends BaseUnitTestMocksTest {

    @InjectMocks
    protected ServiceType service = supplyServiceUnderTest();

    protected abstract ServiceType supplyServiceUnderTest();
}