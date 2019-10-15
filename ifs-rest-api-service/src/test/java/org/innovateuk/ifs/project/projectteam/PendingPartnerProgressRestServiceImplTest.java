package org.innovateuk.ifs.project.projectteam;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PendingPartnerProgressRestServiceImplTest extends BaseRestServiceUnitTest<PendingPartnerProgressRestServiceImpl> {
    private final static String pendingPartnerProgressUrl = "/project/%d/organisation/%d/pending-partner-progress";
    private final static long projectId = 1L;
    private final static long organisationId = 2L;

    @Test
    public void getPendingPartnerProgress() {
        PendingPartnerProgressResource pendingPartnerProgressResource = new PendingPartnerProgressResource();
        setupGetWithRestResultExpectations(format(pendingPartnerProgressUrl, projectId, organisationId), PendingPartnerProgressResource.class, pendingPartnerProgressResource, HttpStatus.OK);

        RestResult<PendingPartnerProgressResource> result = service.getPendingPartnerProgress(projectId, organisationId);

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess(), pendingPartnerProgressResource);
    }

    @Test
    public void markYourOrganisationComplete() {
        setupPostWithRestResultExpectations(format(pendingPartnerProgressUrl, projectId, organisationId) + "/your-organisation-complete", HttpStatus.OK);

        RestResult<Void> result = service.markYourOrganisationComplete(projectId, organisationId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void markYourFundingComplete() {
        setupPostWithRestResultExpectations(format(pendingPartnerProgressUrl, projectId, organisationId) + "/your-funding-complete", HttpStatus.OK);

        RestResult<Void> result = service.markYourFundingComplete(projectId, organisationId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void markTermsAndConditionsComplete() {
        setupPostWithRestResultExpectations(format(pendingPartnerProgressUrl, projectId, organisationId) + "/terms-and-conditions-complete", HttpStatus.OK);

        RestResult<Void> result = service.markTermsAndConditionsComplete(projectId, organisationId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void markYourOrganisationIncomplete() {
        setupPostWithRestResultExpectations(format(pendingPartnerProgressUrl, projectId, organisationId) + "/your-organisation-incomplete", HttpStatus.OK);

        RestResult<Void> result = service.markYourOrganisationIncomplete(projectId, organisationId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void markYourFundingIncomplete() {
        setupPostWithRestResultExpectations(format(pendingPartnerProgressUrl, projectId, organisationId) + "/your-funding-incomplete", HttpStatus.OK);

        RestResult<Void> result = service.markYourFundingIncomplete(projectId, organisationId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void markTermsAndConditionsIncomplete() {
        setupPostWithRestResultExpectations(format(pendingPartnerProgressUrl, projectId, organisationId) + "/terms-and-conditions-incomplete", HttpStatus.OK);

        RestResult<Void> result = service.markTermsAndConditionsIncomplete(projectId, organisationId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void completePartnerSetup() {
        setupPostWithRestResultExpectations(format(pendingPartnerProgressUrl, projectId, organisationId), HttpStatus.OK);

        RestResult<Void> result = service.completePartnerSetup(projectId, organisationId);

        assertTrue(result.isSuccess());
    }


    @Override
    protected PendingPartnerProgressRestServiceImpl registerRestServiceUnderTest() {
        return new PendingPartnerProgressRestServiceImpl();
    }
}