package com.worth.ifs;

import org.mockito.InjectMocks;

/**
 * This is the base class for testing Controllers with mock components.
 *
 */
public abstract class BaseControllerUnitTest<ControllerType> extends BaseUnitTestMocksTest {

    @InjectMocks
    protected ControllerType controller = supplyControllerUnderTest();

    protected abstract ControllerType supplyControllerUnderTest();
}