package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.invite.transactional.InviteService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.invite.builder.InviteResourceBuilder.newInviteResource;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationInviteControllerTest extends BaseControllerMockMVCTest<ApplicationInviteController> {

    @Override
    protected ApplicationInviteController supplyControllerUnderTest() {
        return new ApplicationInviteController();
    }

    @Mock
    private InviteService inviteService;

    @Before
    public void setUp() {
        when(inviteOrganisationRepositoryMock.save(isA(InviteOrganisation.class))).thenReturn(null);
        when(applicationInviteRepositoryMock.save(isA(ApplicationInvite.class))).thenReturn(null);
        when(organisationRepositoryMock.findOne(1L)).thenReturn(newOrganisation().build());
        when(applicationRepositoryMock.findOne(1L)).thenReturn(newApplication().build());
    }

    @Test
    public void postingOrganisationInviteResourceContainingInviteResourcesShouldInitiateSaveCalls() throws Exception {
        List<ApplicationInviteResource> inviteResources = newInviteResource()
                .withApplication(1L)
                .withName("testname")
                .withEmail("testemail")
                .build(5);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResources)
                .withOrganisationName("new organisation")
                .build();

        String organisationResourceString = objectMapper.writeValueAsString(inviteOrganisationResource);

        InviteResultsResource inviteResultsResource = new InviteResultsResource();
        when(inviteService.createApplicationInvites(inviteOrganisationResource)).thenReturn(serviceSuccess(inviteResultsResource));

        mockMvc.perform(post("/invite/createApplicationInvites", "json")
                .contentType(APPLICATION_JSON)
                .content(organisationResourceString))
                .andExpect(status().isCreated())
                .andDo(document("invite/createApplicationInvites"));

        verify(inviteService, times(1)).createApplicationInvites(inviteOrganisationResource);
    }

    @Test
    public void invalidInviteOrganisationResourceShouldReturnErrorMessage() throws Exception {
        List<ApplicationInviteResource> inviteResources = newInviteResource()
                .withApplication(1L)
                .withName("testname")
                .withEmail("testemail")
                .build(5);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResources)
                .build();

        String organisationResourceString = objectMapper.writeValueAsString(inviteOrganisationResource);

        when(inviteService.createApplicationInvites(inviteOrganisationResource)).thenReturn(serviceFailure(badRequestError("no invites")));

        mockMvc.perform(post("/invite/createApplicationInvites", "json")
                .contentType(APPLICATION_JSON)
                .content(organisationResourceString))
                .andExpect(status().isBadRequest());
    }
}
