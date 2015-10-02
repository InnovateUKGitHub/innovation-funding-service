package com.worth.ifs;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Created by dwatson on 02/10/15.
 */
public abstract class BaseRestServiceMocksTest<ServiceType> extends BaseUnitTestMocksTest {

    @InjectMocks
    protected ServiceType service = supplyRestServiceUnderTest();

    protected abstract ServiceType supplyRestServiceUnderTest();
}