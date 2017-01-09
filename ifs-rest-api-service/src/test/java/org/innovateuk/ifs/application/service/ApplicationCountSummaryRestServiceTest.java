package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.applicationCountSummaryResourceListType;
import static org.springframework.http.HttpStatus.OK;

public class ApplicationCountSummaryRestServiceTest extends BaseRestServiceUnitTest<ApplicationCountSummaryRestServiceImpl> {

    @Override
    protected ApplicationCountSummaryRestServiceImpl registerRestServiceUnderTest() {
        return new ApplicationCountSummaryRestServiceImpl();
    }

    @Test
    public void testGetApplicationCountSummariesByCompetitionId() {
        String expectedUrl = "/applicationCountSummary/findByCompetitionId/1";
        List<ApplicationCountSummaryResource> summaryResources = Collections.emptyList();

        setupGetWithRestResultExpectations(expectedUrl, applicationCountSummaryResourceListType(), summaryResources, OK);

        List<ApplicationCountSummaryResource> result = service.getApplicationCountSummariesByCompetitionId(1L).getSuccessObject();
        Assert.assertEquals(summaryResources, result);
    }
}
