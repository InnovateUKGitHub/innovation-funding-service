package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.builder.ApplicationStatusResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationStatusResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.util.MapFunctions;
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
