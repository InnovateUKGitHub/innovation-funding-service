package com.worth.ifs.application.service;

import com.worth.ifs.BaseRestServiceMocksTest;
import com.worth.ifs.application.domain.Application;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.function.Consumer;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

/**
 * Tests to check the ApplicationRestService's interaction with the RestTemplate and the processing of its results
 */
public class ApplicationRestServiceMocksTest extends BaseRestServiceMocksTest<ApplicationRestServiceImpl> {

    private static final String applicationRestURL = "/applications";

    @Override
    protected ApplicationRestServiceImpl registerRestServiceUnderTest(Consumer<ApplicationRestServiceImpl> registrar) {
        ApplicationRestServiceImpl applicationRestService = new ApplicationRestServiceImpl();
        applicationRestService.applicationRestURL = applicationRestURL;
        registrar.accept(applicationRestService);
        return applicationRestService;
    }

    @Test
    public void test_getApplicationById() {

        String expectedUrl = dataServicesUrl + applicationRestURL + "/id/" + 123;
        ResponseEntity<Application> response = new ResponseEntity<>(newApplication().build(), OK);
        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestGet(), Application.class)).thenReturn(response);

        // now run the method under test
        Application application = service.getApplicationById(123L);
        assertNotNull(application);
    }
}
