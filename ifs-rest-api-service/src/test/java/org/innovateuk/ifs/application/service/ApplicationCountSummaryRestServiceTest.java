package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.applicationCountSummaryResourceListType;
import static org.springframework.http.HttpStatus.OK;

public class ApplicationCountSummaryRestServiceTest extends BaseRestServiceUnitTest<ApplicationCountSummaryRestServiceImpl> {

    @Override
    protected ApplicationCountSummaryRestServiceImpl registerRestServiceUnderTest() {
        return new ApplicationCountSummaryRestServiceImpl();
    }

    @Ignore
    @Test
    public void getApplicationCountSummariesByCompetitionId() {
        String expectedUrl = "/applicationCountSummary/findByCompetitionId/1?page=2&size=3&filter=filter";
        ApplicationCountSummaryPageResource pageResource = new ApplicationCountSummaryPageResource();

        setupGetWithRestResultExpectations(expectedUrl, ApplicationCountSummaryPageResource.class, pageResource, OK);

        ApplicationCountSummaryPageResource result = service.getApplicationCountSummariesByCompetitionId(1L, 2, 3, "filter").getSuccessObject();
        Assert.assertEquals(pageResource, result);
    }
}
