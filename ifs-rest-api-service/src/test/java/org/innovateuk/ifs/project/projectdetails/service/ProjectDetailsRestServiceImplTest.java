package org.innovateuk.ifs.project.projectdetails.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class ProjectDetailsRestServiceImplTest extends BaseRestServiceUnitTest<ProjectDetailsRestServiceImpl> {
    private static final String projectRestURL = "/project";

    @Override
    protected ProjectDetailsRestServiceImpl registerRestServiceUnderTest() {
        ProjectDetailsRestServiceImpl projectDetailsService = new ProjectDetailsRestServiceImpl();
        ReflectionTestUtils.setField(projectDetailsService, "projectRestURL", projectRestURL);
        return projectDetailsService;
    }

    @Test
    public void testUpdateProjectDuration() {
        long projectId = 3L;
        long durationInMonths = 18L;

        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/duration/" + durationInMonths, null, OK);
        RestResult<Void> result = service.updateProjectDuration(projectId, durationInMonths);
        assertTrue(result.isSuccess());

        setupPostWithRestResultVerifications(projectRestURL + "/" + projectId + "/duration/" + durationInMonths, Void.class, null);
    }

    @Test
    public void testUpdateFinanceContact() {
        setupPostWithRestResultExpectations(projectRestURL + "/123/organisation/5/finance-contact?financeContact=6", null, OK);
        RestResult<Void> result = service.updateFinanceContact(new ProjectOrganisationCompositeId(123L, 5L), 6L);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testUpdatePartnerProjectLocation() {

        long projectId = 1L;
        long organisationId = 2L;
        String postcode = "TW14 9QG";
        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/organisation/" + organisationId + "/partner-project-location?postcode=" + postcode, null, OK);

        RestResult<Void> result = service.updatePartnerProjectLocation(projectId, organisationId, postcode);
        assertTrue(result.isSuccess());

        setupPostWithRestResultVerifications(projectRestURL + "/" + projectId + "/organisation/" + organisationId + "/partner-project-location?postcode=" + postcode, Void.class, null);
    }

    @Test
    public void testUpdateProjectAddress() {

        AddressResource addressResource = new AddressResource();

        setupPostWithRestResultExpectations(projectRestURL + "/123/address?leadOrganisationId=456", addressResource, OK);

        RestResult<Void> result = service.updateProjectAddress(456L, 123L, addressResource);

        assertTrue(result.isSuccess());

    }
}
