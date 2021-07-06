package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.crm.transactional.CrmService;
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

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
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
                .andExpect(status().isCreated())
                .andDo(document("invite/create-application-invites/" + applicationId));

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

        mockMvc.perform(post("/invite/create-application-invites/"+applicationId, "json")
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

        when(acceptApplicationInviteService.acceptInvite(hash, userId, Optional.empty())).thenReturn(serviceSuccess());

        mockMvc.perform(put("/invite/accept-invite/{hash}/{userId}", hash, userId))
                .andExpect(status().isOk());

        verify(acceptApplicationInviteService).acceptInvite(hash, userId, Optional.empty());
        verify(crmService).syncCrmContact(userId);
    }

    @Test
    public void acceptInvite_withOrganisationId() throws Exception {
        String hash = "abcdef";
        long userId = 1L;
        long organisationId = 2L;

        when(acceptApplicationInviteService.acceptInvite(hash, userId, Optional.of(organisationId))).thenReturn(serviceSuccess());

        mockMvc.perform(put("/invite/accept-invite/{hash}/{userId}/{organisationId}", hash, userId, organisationId))
                .andExpect(status().isOk());

        verify(acceptApplicationInviteService).acceptInvite(hash, userId, Optional.of(organisationId));
        verify(crmService).syncCrmContact(userId);
    }
}
