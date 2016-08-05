package com.worth.ifs.invite.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.invite.domain.ApplicationInvite;
import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import com.worth.ifs.invite.transactional.InviteService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.commons.error.CommonErrors.badRequestError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static com.worth.ifs.invite.builder.InviteResourceBuilder.newInviteResource;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
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

        ObjectMapper mapper = new ObjectMapper();
        String organisationResourceString = mapper.writeValueAsString(inviteOrganisationResource);

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

        ObjectMapper mapper = new ObjectMapper();
        String organisationResourceString = mapper.writeValueAsString(inviteOrganisationResource);

        when(inviteService.createApplicationInvites(inviteOrganisationResource)).thenReturn(serviceFailure(badRequestError("no invites")));

        mockMvc.perform(post("/invite/createApplicationInvites", "json")
                .contentType(APPLICATION_JSON)
                .content(organisationResourceString))
                .andExpect(status().isBadRequest());
    }
}