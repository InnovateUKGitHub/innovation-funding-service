package org.innovateuk.ifs.application.team.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.team.form.ApplicantInviteForm;
import org.innovateuk.ifs.application.team.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.team.populator.ApplicationTeamManagementModelPopulator;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteOrganisationRestService;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class ExistingOrganisationTeamManagementServiceTest extends BaseServiceUnitTest<ExistingOrganisationTeamManagementService> {

    private long applicationId = 123L;
    private long organisationId = 456L;

    @Mock
    private ApplicationTeamManagementModelPopulator applicationTeamManagementModelPopulator;

    @Mock
    private UserRestService userRestService;

    @Mock
    private InviteOrganisationRestService inviteOrganisationRestServiceMock;

    @Mock
    private InviteRestService inviteRestServiceMock;

    protected ExistingOrganisationTeamManagementService supplyServiceUnderTest() {
        return new ExistingOrganisationTeamManagementService(userRestService);
    }

    @Before
    public void setUp() {
        super.setup();
        applicationId = 1L;
        organisationId = 2L;
    }

    @Test
    public void createViewModel_populatorShouldBeAppropriateParameters() throws Exception {
        UserResource userResource = newUserResource().build();

        ApplicationTeamManagementViewModel expectedModel = new ApplicationTeamManagementViewModel(applicationId,
                1L,
                null,
                organisationId,
                1L,
                null,
                false,
                false,
                null ,
                false);

        when(applicationTeamManagementModelPopulator.populateModelByOrganisationId(applicationId, organisationId, userResource.getId())).thenReturn(expectedModel);

        ApplicationTeamManagementViewModel result = service.createViewModel(applicationId, organisationId, userResource);

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
                .withInviteOrganisation(organisationId)
                .withHash().build();

        when(inviteRestServiceMock.createInvitesByOrganisationForApplication(any(), any(), any())).thenReturn(restSuccess());

        ServiceResult<Void> result = service.executeStagedInvite(applicationId, organisationId, form);

        assertTrue(result.isSuccess());
        verify(inviteRestServiceMock, times(1)).createInvitesByOrganisationForApplication(applicationId, organisationId, Arrays.asList(expectedInviteResource));
    }

    @Test
    public void validateOrganisationAndApplicationIds_whenAnyProcessRolesOrganisationsMatchOrganisationIdTrueShouldBeReturned() throws Exception {

        List<ProcessRoleResource> processRoleResourceList = newProcessRoleResource().
                withApplication(applicationId).
                withOrganisation(987L, organisationId, 654L).
                build(3);

        when(inviteOrganisationRestServiceMock.getByOrganisationIdWithInvitesForApplication(organisationId, applicationId)).
                thenReturn(restFailure(notFoundError(InviteOrganisationResource.class)));
        when(userRestService.findProcessRole(applicationId)).thenReturn(restSuccess(processRoleResourceList));

        boolean result = service.applicationAndOrganisationIdCombinationIsValid(applicationId, organisationId);

        assertTrue(result);

        verify(inviteOrganisationRestServiceMock).getByOrganisationIdWithInvitesForApplication(organisationId, applicationId);
        verify(userRestService).findProcessRole(applicationId);
    }

    @Test
    public void validateOrganisationAndApplicationIds_whenNoProcessRolesOrganisationsMatchOrganisationIdFalseShouldBeReturned() throws Exception {

        List<ProcessRoleResource> processRoleResourceList = newProcessRoleResource().
                withApplication(applicationId).
                withOrganisation(987L, 654L).
                build(2);

        when(inviteOrganisationRestServiceMock.getByOrganisationIdWithInvitesForApplication(organisationId, applicationId)).
                thenReturn(restFailure(notFoundError(InviteOrganisationResource.class)));
        when(userRestService.findProcessRole(applicationId)).thenReturn(restSuccess(processRoleResourceList));

        boolean result = service.applicationAndOrganisationIdCombinationIsValid(applicationId, organisationId);

        assertFalse(result);

        verify(inviteOrganisationRestServiceMock).getByOrganisationIdWithInvitesForApplication(organisationId, applicationId);
        verify(userRestService).findProcessRole(applicationId);
    }

    @Test
    public void validateOrganisationAndApplicationIds_whenNoProcessRolesAreFoundFalseShouldBeReturned() throws Exception {

        when(inviteOrganisationRestServiceMock.getByOrganisationIdWithInvitesForApplication(organisationId, applicationId)).
                thenReturn(restFailure(notFoundError(InviteOrganisationResource.class)));
        when(userRestService.findProcessRole(applicationId)).thenReturn(restSuccess(new ArrayList<>()));

        boolean result = service.applicationAndOrganisationIdCombinationIsValid(applicationId, organisationId);

        assertFalse(result);

        verify(inviteOrganisationRestServiceMock).getByOrganisationIdWithInvitesForApplication(organisationId, applicationId);
        verify(userRestService).findProcessRole(applicationId);
    }

    @Test
    public void validateOrganisationAndApplicationIds_whenInviteOrganisationExistsTrueShouldBeReturned() throws Exception {

        when(inviteOrganisationRestServiceMock.getByOrganisationIdWithInvitesForApplication(organisationId, applicationId)).
                thenReturn(restSuccess(newInviteOrganisationResource().build()));

        boolean result = service.applicationAndOrganisationIdCombinationIsValid(applicationId, organisationId);

        assertTrue(result);

        verify(inviteOrganisationRestServiceMock).getByOrganisationIdWithInvitesForApplication(organisationId, applicationId);
        verify(userRestService, never()).findProcessRole(applicationId);
    }

    @Test
    public void getInviteIds_foundIdsShouldBeMappedToReturnedList() throws Exception {

        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource().withId(1L,2L,3L,4L,5L).withApplication(2L).build(5);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResources).build();

        when(inviteOrganisationRestServiceMock.getByOrganisationIdWithInvitesForApplication(organisationId, applicationId)).thenReturn(restSuccess(inviteOrganisationResource));

        List<Long> result = service.getInviteIds(applicationId, organisationId);

        assertTrue(inviteOrganisationResource.getInviteResources().stream().allMatch(invite -> result.contains(invite.getId())));
    }

    @Test
    public void getInviteIds_noIdsFoundShouldReturnEmptyList() throws Exception {
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().build();

        when(inviteOrganisationRestServiceMock.getByOrganisationIdWithInvitesForApplication(organisationId, applicationId)).thenReturn(restSuccess(inviteOrganisationResource));

        List<Long> result = service.getInviteIds(applicationId, organisationId);

        assertTrue(result.isEmpty());

    }

    @Test(expected=RuntimeException.class)
    public void getInviteIds_inviteOrganisationNotFoundShouldReturnException() throws Exception {
        when(inviteOrganisationRestServiceMock.getByOrganisationIdWithInvitesForApplication(organisationId, applicationId)).thenReturn(RestResult.restFailure(new Error("BAD_REQUEST", BAD_REQUEST)));

        service.getInviteIds(applicationId, organisationId);
    }
}