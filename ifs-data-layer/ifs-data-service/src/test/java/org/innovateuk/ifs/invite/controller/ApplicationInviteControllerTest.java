package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.crm.transactional.CrmService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.transactional.AcceptApplicationInviteService;
import org.innovateuk.ifs.invite.transactional.ApplicationInviteService;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationInviteControllerTest extends BaseControllerMockMVCTest<ApplicationInviteController> {

    @Override
    protected ApplicationInviteController supplyControllerUnderTest() {
        return new ApplicationInviteController();
    }

    @Mock
    private ApplicationInviteService applicationInviteService;

    @Mock
    private AcceptApplicationInviteService acceptApplicationInviteService;

    @Mock
    private InviteOrganisationRepository inviteOrganisationRepositoryMock;

    @Mock
    private ApplicationInviteRepository applicationInviteRepositoryMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private CrmService crmService;

    @Before
    public void setUp() {
        when(inviteOrganisationRepositoryMock.save(isA(InviteOrganisation.class))).thenReturn(null);
        when(applicationInviteRepositoryMock.save(isA(ApplicationInvite.class))).thenReturn(null);
        when(organisationRepositoryMock.findById(1L)).thenReturn(Optional.of(newOrganisation().build()));
        when(applicationRepositoryMock.findById(1L)).thenReturn(Optional.of(newApplication().build()));
    }

    @Test
    public void postingOrganisationInviteResourceContainingInviteResourcesShouldInitiateSaveCalls() throws Exception {
        long applicationId = 1L;
        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource()
                .withApplication(1L)
                .withName("testname")
                .withEmail("testemail")
                .build(5);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResources)
                .withOrganisationName("new organisation")
                .build();

        String organisationResourceString = objectMapper.writeValueAsString(inviteOrganisationResource);

        when(applicationInviteService.createApplicationInvites(inviteOrganisationResource, Optional.of(applicationId))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/invite/create-application-invites/" + applicationId, "json")
                        .header("IFS_AUTH_TOKEN", "123abc")
                        .contentType(APPLICATION_JSON)
                        .content(organisationResourceString))
                .andExpect(status().isCreated());

        verify(applicationInviteService, times(1)).createApplicationInvites(inviteOrganisationResource, Optional.of(applicationId));
    }

    @Test
    public void invalidInviteOrganisationResourceShouldReturnErrorMessage() throws Exception {

        long applicationId = 1L;
        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource()
                .withApplication(1L)
                .withName("testname")
                .withEmail("testemail")
                .build(5);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResources)
                .build();

        String organisationResourceString = objectMapper.writeValueAsString(inviteOrganisationResource);

        when(applicationInviteService.createApplicationInvites(inviteOrganisationResource, Optional.of(applicationId))).thenReturn(serviceFailure(badRequestError("no invites")));

        mockMvc.perform(post("/invite/create-application-invites/" + applicationId, "json")
                        .contentType(APPLICATION_JSON)
                        .content(organisationResourceString))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void resendInvite() throws Exception {

        long applicationId = 1L;
        ApplicationInviteResource inviteResource = newApplicationInviteResource()
                .withApplication(applicationId)
                .withName("testname")
                .withEmail("testemail")
                .build();

        when(applicationInviteService.resendInvite(inviteResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/invite/resend-invite")
                        .contentType(APPLICATION_JSON)
                        .content(toJson(inviteResource)))
                .andExpect(status().isCreated());
    }

    @Test
    public void acceptInvite() throws Exception {
        String hash = "abcdef";
        long userId = 1L;
        Long compId = 1L;
        Long appId = 1L;
        when(acceptApplicationInviteService.acceptInvite(hash, userId, Optional.empty())).thenReturn(serviceSuccess());
        ApplicationInviteResource inviteResource = newApplicationInviteResource().withApplication(appId).withCompetitionId(compId).build();
        when(applicationInviteService.getInviteByHash(anyString())).thenReturn(serviceSuccess(inviteResource));
        mockMvc.perform(put("/invite/accept-invite/{hash}/{userId}", hash, userId))
                .andExpect(status().isOk());

        verify(acceptApplicationInviteService).acceptInvite(hash, userId, Optional.empty());
        verify(crmService).syncCrmContact(userId, compId, appId);
    }

    @Test
    public void acceptInvite_withOrganisationId() throws Exception {
        String hash = "abcdef";
        long userId = 1L;
        long organisationId = 2L;
        Long compId = 1L;
        Long appId = 1L;

        ApplicationInviteResource inviteResource = newApplicationInviteResource().withApplication(appId).withCompetitionId(compId).build();
        when(applicationInviteService.getInviteByHash(anyString())).thenReturn(serviceSuccess(inviteResource));
        when(acceptApplicationInviteService.acceptInvite(hash, userId, Optional.of(organisationId))).thenReturn(serviceSuccess());

        mockMvc.perform(put("/invite/accept-invite/{hash}/{userId}/{organisationId}", hash, userId, organisationId))
                .andExpect(status().isOk());

        verify(acceptApplicationInviteService).acceptInvite(hash, userId, Optional.of(organisationId));
        verify(crmService).syncCrmContact(userId, compId, appId);
    }

    @Test
    public void acceptInvite_withApplicationInviteResource() throws Exception {
        String hash = "abcdef";
        long userId = 1L;
        long organisationId = 2L;

        String expectedLeadApplicant = "Steve Smith";
        String expectedLeadApplicantEmail = "steve.smith@empire";
        String expectedLeadOrganisation = "Empire";
        String expectedName = "Jessica Doe";
        String expectedNameConfirmed = "Jessica Doe";
        String expectedEmail = "jessica.doe@ludlow.co.uk";
        InviteStatus expectedStatus = SENT;
        Long expectedApplication = 1L;
        Long expectedUser = 2L;
        String expectedHash = "hash";
        Long expectedInviteOrganisation = 3L;
        ApplicationInviteResource applicationInviteResource = newApplicationInviteResource()
                .withLeadApplicant(expectedLeadApplicant)
                .withLeadApplicantEmail(expectedLeadApplicantEmail)
                .withLeadOrganisation(expectedLeadOrganisation)
                .withName(expectedName)
                .withNameConfirmed(expectedNameConfirmed)
                .withEmail(expectedEmail)
                .withStatus(expectedStatus)
                .withApplication(expectedApplication)
                .withUsers(expectedUser)
                .withHash(expectedHash)
                .withInviteOrganisation(expectedInviteOrganisation)
                .build();
        when(applicationInviteService.updateInviteHistory(applicationInviteResource)).thenReturn(serviceSuccess());
        when(acceptApplicationInviteService.acceptInvite(hash, userId, Optional.of(organisationId))).thenReturn(serviceSuccess());

        mockMvc.perform(put("/invite/update-invite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(applicationInviteResource)))
                .andExpect(status().isOk());

        verify(applicationInviteService).updateInviteHistory(applicationInviteResource);

    }

}
