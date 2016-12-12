package com.worth.ifs.application.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.builder.ApplicationStatusResourceBuilder;
import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.util.MapFunctions;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ApplicationStatusRestServiceMocksTest extends BaseRestServiceUnitTest<ApplicationStatusRestServiceImpl> {

    private static final String applicationStatusRestURL = "/applicationstatus";

    @Override
    protected ApplicationStatusRestServiceImpl registerRestServiceUnderTest() {
        return new ApplicationStatusRestServiceImpl();
    }

    @Test
    public void testGetApplicationStatusById() {
        Long applicationId = 123L;
        ApplicationStatusResource applicationStatusResource = ApplicationStatusResourceBuilder.newApplicationStatusResource().build();

        String expectedUrl = applicationStatusRestURL + "/" + 123;
        setupGetWithRestResultExpectations(expectedUrl, ApplicationStatusResource.class, applicationStatusResource);

        RestResult<ApplicationStatusResource> result = service.getApplicationStatusById(applicationId);
        assertNotNull(result);
        assertEquals(result.getSuccessObjectOrThrowException(), applicationStatusResource);
    }
}