package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InviteOrganisationControllerTest extends BaseControllerMockMVCTest<InviteOrganisationController> {

    @Override
    protected InviteOrganisationController supplyControllerUnderTest() {
        return new InviteOrganisationController();
    }

    @Test
    public void getByIdWithInvitesForApplication() throws Exception {
        long applicationId = 1L;

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().build();

        when(inviteOrganisationServiceMock.getByIdWithInvitesForApplication(inviteOrganisationResource.getId(), applicationId))
                .thenReturn(serviceSuccess(inviteOrganisationResource));

        mockMvc.perform(get("/inviteorganisation/{id}/application/{applicationId}",
                inviteOrganisationResource.getId(), applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(inviteOrganisationResource)));

        verify(inviteOrganisationServiceMock, only()).getByIdWithInvitesForApplication(inviteOrganisationResource.getId(), applicationId);
    }

    @Test
    public void getByOrganisationIdWithInvitesForApplication() throws Exception {
        long organisationId = 1L;
        long applicationId = 2L;

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().build();

        when(inviteOrganisationServiceMock.getByOrganisationIdWithInvitesForApplication(inviteOrganisationResource.getId(), applicationId))
                .thenReturn(serviceSuccess(inviteOrganisationResource));

        mockMvc.perform(get("/inviteorganisation/organisation/{organisationId}/application/{applicationId}",
                organisationId, applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(inviteOrganisationResource)));

        verify(inviteOrganisationServiceMock, only()).getByOrganisationIdWithInvitesForApplication(organisationId, applicationId);
    }
}