package com.worth.ifs;

import com.worth.ifs.commons.service.BaseRestServiceProvider;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by dwatson on 02/10/15.
 */
public abstract class BaseRestServiceMocksTest<ServiceType extends BaseRestServiceProvider> extends BaseUnitTestMocksTest {

    @Mock
    protected RestTemplate mockRestTemplate;

    protected ServiceType service;

    protected abstract ServiceType registerRestServiceUnderTest(Consumer<ServiceType> registrar);

    protected static final String dataServicesUrl = "http://localhost/tests/dataServices";

    @Override
    public void setUp() {

        super.setUp();

        service = registerRestServiceUnderTest(s -> {
            s.setDataRestServiceUrl(dataServicesUrl);
            s.setRestTemplateSupplier(() -> mockRestTemplate);
        });
    }
}