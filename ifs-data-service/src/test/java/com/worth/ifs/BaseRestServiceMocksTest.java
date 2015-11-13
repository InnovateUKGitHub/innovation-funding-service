package com.worth.ifs;

import com.worth.ifs.commons.service.BaseRestServiceProvider;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

import java.util.function.Consumer;

/**
 * This is the base class for testing REST services with mock components.  In addition to the standard mocks provided,
 * this base class also provides a dummy dataServiceUrl and a mock restTemplate for testing and stubbing the routes
 * that the REST services use to exchange data with the "data" layer.
 *
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