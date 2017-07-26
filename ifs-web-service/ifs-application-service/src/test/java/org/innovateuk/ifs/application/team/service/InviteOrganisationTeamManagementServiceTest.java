package org.innovateuk.ifs.application.team.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.team.form.ApplicantInviteForm;
import org.innovateuk.ifs.application.team.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.team.populator.ApplicationTeamManagementModelPopulator;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class InviteOrganisationTeamManagementServiceTest extends BaseServiceUnitTest<InviteOrganisationTeamManagementService> {

    @Mock
    private ApplicationTeamManagementModelPopulator applicationTeamManagementModelPopulator;

    private long applicationId;
    private long inviteOrganisationId;

    @Before
    public void setUp() {
        super.setUp();
        applicationId = 1L;
        inviteOrganisationId = 2L;
    }

    protected InviteOrganisationTeamManagementService supplyServiceUnderTest() {
        return new InviteOrganisationTeamManagementService();
    }

    @Test
    public void createViewModel_populatorShouldBeCalledWithAppropriateParameters() throws Exception {
        UserResource userResource = newUserResource().build();

        ApplicationTeamManagementViewModel expectedModel = new ApplicationTeamManagementViewModel(1L,
                null,
                1L,
                1L,
                null,
                false,
                false,
                null ,
                false);

        when(applicationTeamManagementModelPopulator.populateModelByInviteOrganisationId(applicationId, inviteOrganisationId, userResource.getId())).thenReturn(expectedModel);

        ApplicationTeamManagementViewModel result = service.createViewModel(applicationId, inviteOrganisationId, userResource);

        assertEquals(expectedModel, result);
    }

    @Test
    public void executeStagedInvite_callSaveInviteShouldBeCalledWithCorrectlyMappedInvite() throws Exception {
        String email = "email@test.test";
        String name = "firstname";

        ApplicantInviteForm inviteForm = new ApplicantInviteForm();
        inviteForm.setEmail(email);
        inviteForm.setName(name);

        ApplicationTeamUpdateForm form = new ApplicationTeamUpdateForm();
        form.setStagedInvite(inviteForm);

        ApplicationInviteResource expectedInviteResource = newApplicationInviteResource()
                .withId((Long) null)
                .withEmail(email)
                .withName(name)
                .withApplication(applicationId)
                .withInviteOrganisation(inviteOrganisationId)
                .withHash().build();

        InviteResultsResource expectedInviteResultsResource = new InviteResultsResource();

        when(inviteRestServiceMock.saveInvites(any())).thenReturn(RestResult.restSuccess(expectedInviteResultsResource));

        InviteResultsResource result = service.executeStagedInvite(applicationId, inviteOrganisationId, form).getSuccessObject();

        assertEquals(result, expectedInviteResultsResource);
        verify(inviteRestServiceMock, times(1)).saveInvites(Arrays.asList(expectedInviteResource));
    }

    @Test
    public void validateOrganisationAndApplicationIds_trueShouldBeReturnedWhenInviteOrganisationContainsInvitesForApplication() throws Exception {
        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource().withApplication(1L).build(5);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResources).build();

        when(inviteOrganisationRestServiceMock.getById(inviteOrganisationId)).thenReturn(RestResult.restSuccess(inviteOrganisationResource));

        boolean result = service.applicationAndOrganisationIdCombinationIsValid(applicationId, inviteOrganisationId);

        assertTrue(result);
    }

    @Test
    public void validateOrganisationAndApplicationIds_falseShouldBeReturnedWhenInviteOrganisationLacksInvitesForApplication() throws Exception {
        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource().withApplication(2L).build(5);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResources).build();

        when(inviteOrganisationRestServiceMock.getById(inviteOrganisationId)).thenReturn(RestResult.restSuccess(inviteOrganisationResource));

        boolean result = service.applicationAndOrganisationIdCombinationIsValid(applicationId, inviteOrganisationId);

        assertFalse(result);
    }

    @Test(expected=RuntimeException.class)
    public void validateOrganisationAndApplicationIds_exceptionShouldBeThrownWhenTheOrganisationIsNotFound() throws Exception {
        when(inviteOrganisationRestServiceMock.getById(inviteOrganisationId)).thenReturn(RestResult.restFailure(new Error("BAD_REQUEST", BAD_REQUEST)));

        service.applicationAndOrganisationIdCombinationIsValid(applicationId, inviteOrganisationId);
    }

    @Test
    public void getInviteIds_foundIdsShouldBeMappedToReturnedList() throws Exception {
        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource().withId(1L,2L,3L,4L,5L).withApplication(2L).build(5);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResources).build();

        when(inviteOrganisationRestServiceMock.getById(inviteOrganisationId)).thenReturn(RestResult.restSuccess(inviteOrganisationResource));

        List<Long> result = service.getInviteIds(applicationId, inviteOrganisationId);

        assertTrue(inviteOrganisationResource.getInviteResources().stream().allMatch(invite -> result.contains(invite.getId())));
    }

    @Test
    public void getInviteIds_noIdsFoundShouldReturnEmptyList() throws Exception {
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().build();

        when(inviteOrganisationRestServiceMock.getById(inviteOrganisationId)).thenReturn(RestResult.restSuccess(inviteOrganisationResource));

        List<Long> result = service.getInviteIds(applicationId, inviteOrganisationId);

        assertTrue(result.isEmpty());

    }

    @Test(expected=RuntimeException.class)
    public void getInviteIds_organisationNotFoundShouldReturnException() throws Exception {
        when(inviteOrganisationRestServiceMock.getById(inviteOrganisationId)).thenReturn(RestResult.restFailure(new Error("BAD_REQUEST", BAD_REQUEST)));

        service.getInviteIds(applicationId, inviteOrganisationId);
    }
}