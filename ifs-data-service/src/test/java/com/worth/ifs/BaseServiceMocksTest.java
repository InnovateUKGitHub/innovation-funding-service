package com.worth.ifs;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * This is the base class for testing Services with mock components.
 *
 * Created by dwatson on 02/10/15.
 */
public abstract class BaseServiceMocksTest<ServiceType> extends BaseUnitTestMocksTest {

    @InjectMocks
    protected ServiceType service = supplyServiceUnderTest();

    protected abstract ServiceType supplyServiceUnderTest();
}