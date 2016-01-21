package com.worth.ifs.invite.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.resource.ResourceEnvelopeConstants;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static com.worth.ifs.invite.builder.InviteResourceBuilder.newInviteResource;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class InviteControllerTest extends BaseControllerMockMVCTest<InviteController> {
    @Override
    protected InviteController supplyControllerUnderTest() {
        return new InviteController();
    }

    @Before
    public void setUp() {
        when(inviteOrganisationRepositoryMock.save(isA(InviteOrganisation.class))).thenReturn(null);
        when(inviteRepositoryMock.save(isA(Invite.class))).thenReturn(null);
        when(organisationRepositoryMock.findOne(1L)).thenReturn(newOrganisation().build());
        when(applicationRepositoryMock.findOne(1L)).thenReturn(newApplication().build());
    }

    @Test
    public void postingOrganisationInviteResourceContainingInviteResourcesShouldInitiateSaveCalls() throws Exception {
        List<InviteResource> inviteResources = newInviteResource()
                .withApplicationId(1L)
                .withName("testname")
                .withEmail("testemail")
                .build(5);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResources)
                .withOrganisationName("new organisation")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String organisationResourceString = mapper.writeValueAsString(inviteOrganisationResource);

        mockMvc.perform(post("/invite/createApplicationInvites", "json")
                .contentType(APPLICATION_JSON)
                .content(organisationResourceString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(ResourceEnvelopeConstants.OK.getName())))
                .andDo(document("invite/createApplicationInvites"));

        verify(inviteRepositoryMock, times(1)).save(Matchers.anyListOf(Invite.class));
        verify(inviteOrganisationRepositoryMock, times(1)).save(Matchers.isA(InviteOrganisation.class));
    }

    @Test
    public void validInviteOrganisationResourceWithOrganisationNameShouldReturnSuccessMessage() throws Exception{
        List<InviteResource> inviteResources = newInviteResource()
        .withApplicationId(1L)
        .withName("testname")
        .withEmail("testemail")
        .build(5);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
        .withInviteResources(inviteResources)
        .withOrganisationName("new organisation")
        .build();

        ObjectMapper mapper = new ObjectMapper();
        String organisationResourceString = mapper.writeValueAsString(inviteOrganisationResource);

        mockMvc.perform(post("/invite/createApplicationInvites", "json")
        .contentType(APPLICATION_JSON)
        .content(organisationResourceString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is(ResourceEnvelopeConstants.OK.getName())))
        .andDo(document("invite/createApplicationInvites"));
    }

    @Test
    public void validInviteOrganisationResourceWithOrganisationIdShouldReturnSuccessMessage() throws Exception{
        List<InviteResource> inviteResources = newInviteResource()
                .withApplicationId(1L)
                .withName("testname")
                .withEmail("testemail")
                .build(5);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResources)
                .withOrganisationId(1L)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String organisationResourceString = mapper.writeValueAsString(inviteOrganisationResource);

        mockMvc.perform(post("/invite/createApplicationInvites", "json")
                .contentType(APPLICATION_JSON)
                .content(organisationResourceString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(ResourceEnvelopeConstants.OK.getName())))
                .andDo(document("invite/createApplicationInvites"));
    }

    @Test
    public void invalidInviteOrganisationResourceMissingOrganisationNameAndIdShouldReturnErrorMessage() throws Exception{
        List<InviteResource> inviteResources = newInviteResource()
                .withApplicationId(1L)
                .withName("testname")
                .withEmail("testemail")
                .build(5);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResources)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String organisationResourceString = mapper.writeValueAsString(inviteOrganisationResource);

        mockMvc.perform(post("/invite/createApplicationInvites", "json")
                .contentType(APPLICATION_JSON)
                .content(organisationResourceString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(ResourceEnvelopeConstants.ERROR.getName())))
                .andDo(document("invite/createApplicationInvites"));
    }

    @Test
     public void validOrganisationResourceWithInvalidInviteResourceMissingNameShouldReturnError() throws Exception{
        List<InviteResource> invalidInviteResourceMissingName = newInviteResource()
                .withApplicationId(1L)
                .withEmail("testemail")
                .build(1);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(invalidInviteResourceMissingName)
                .withOrganisationName("new organisation")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String organisationResourceString = mapper.writeValueAsString(inviteOrganisationResource);

        mockMvc.perform(post("/invite/createApplicationInvites", "json")
                .contentType(APPLICATION_JSON)
                .content(organisationResourceString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(ResourceEnvelopeConstants.ERROR.getName())))
                .andDo(document("invite/createApplicationInvites"));
    }

    @Test
    public void validOrganisationResourceWithInvalidInviteResourceMissingEmailShouldReturnError() throws Exception{
        List<InviteResource> invalidInviteResourceMissingEmail = newInviteResource()
                .withApplicationId(1L)
                .withName("testName")
                .build(1);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(invalidInviteResourceMissingEmail)
                .withOrganisationName("new organisation")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String organisationResourceString = mapper.writeValueAsString(inviteOrganisationResource);

        mockMvc.perform(post("/invite/createApplicationInvites", "json")
                .contentType(APPLICATION_JSON)
                .content(organisationResourceString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(ResourceEnvelopeConstants.ERROR.getName())))
                .andDo(document("invite/createApplicationInvites"));
    }

    @Test
    public void validOrganisationResourceWithInvalidInviteResourceMissingApplicationIdShouldReturnError() throws Exception{
        List<InviteResource> invalidInviteResourceMissingApplicationId = newInviteResource()
                .withName("testName")
                .withEmail("testemail")
                .build(1);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(invalidInviteResourceMissingApplicationId)
                .withOrganisationName("new organisation")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String organisationResourceString = mapper.writeValueAsString(inviteOrganisationResource);

        mockMvc.perform(post("/invite/createApplicationInvites", "json")
                .contentType(APPLICATION_JSON)
                .content(organisationResourceString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(ResourceEnvelopeConstants.ERROR.getName())))
                .andDo(document("invite/createApplicationInvites"));
    }
}