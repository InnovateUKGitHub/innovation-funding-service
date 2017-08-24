package org.innovateuk.ifs.project.projectdetails.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeEnum;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Assert;
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
    public void testUpdateFinanceContact() {
        setupPostWithRestResultExpectations(projectRestURL + "/123/organisation/5/finance-contact?financeContact=6", null, OK);
        RestResult<Void> result = service.updateFinanceContact(new ProjectOrganisationCompositeId(123L, 5L), 6L);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testUpdateProjectAddress() {

        AddressResource addressResource = new AddressResource();

        setupPostWithRestResultExpectations(projectRestURL + "/123/address?addressType=" + AddressTypeEnum.REGISTERED.name() + "&leadOrganisationId=456", addressResource, OK);

        RestResult<Void> result = service.updateProjectAddress(456L, 123L, AddressTypeEnum.REGISTERED, addressResource);

        assertTrue(result.isSuccess());

    }

    @Test
    public void testSetApplicationDetailsSubmitted() {
        setupPostWithRestResultExpectations(projectRestURL + "/" + 123L + "/setApplicationDetailsSubmitted", null, OK);

        RestResult<Void> result = service.setApplicationDetailsSubmitted(123L);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testIsSubmitAllowed() {
        Boolean isAllowed = true;

        setupGetWithRestResultExpectations(projectRestURL + "/" + 123L + "/isSubmitAllowed", Boolean.class, isAllowed);

        RestResult<Boolean> result = service.isSubmitAllowed(123L);

        assertTrue(result.isSuccess());

        Assert.assertEquals(isAllowed, result.getSuccessObject());
    }

    @Test
    public void testInviteProjectManager() {
        long projectId = 123L;
        InviteProjectResource invite = new InviteProjectResource();

        String expectedUrl = projectRestURL + "/" + projectId + "/invite-project-manager";
        setupPostWithRestResultExpectations(expectedUrl, invite, OK);

        RestResult<Void> result = service.inviteProjectManager(projectId, invite);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testInviteFinanceContact() {
        long projectId = 123L;
        InviteProjectResource invite = new InviteProjectResource();

        String expectedUrl = projectRestURL + "/" + projectId + "/invite-finance-contact";
        setupPostWithRestResultExpectations(expectedUrl, invite, OK);

        RestResult<Void> result = service.inviteFinanceContact(projectId, invite);

        assertTrue(result.isSuccess());
    }
}
