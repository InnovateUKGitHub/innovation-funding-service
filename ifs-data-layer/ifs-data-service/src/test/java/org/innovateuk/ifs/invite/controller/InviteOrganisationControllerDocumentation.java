package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.transactional.InviteOrganisationService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.InviteOrganisationDocs.inviteOrganisationResourceBuilder;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InviteOrganisationControllerDocumentation extends BaseControllerMockMVCTest<InviteOrganisationController> {

    @Mock
    private InviteOrganisationService inviteOrganisationServiceMock;

    @Override
    protected InviteOrganisationController supplyControllerUnderTest() {
        return new InviteOrganisationController();
    }

    @Test
    public void getById() throws Exception {
        InviteOrganisationResource inviteOrganisationResource = inviteOrganisationResourceBuilder.build();

        when(inviteOrganisationServiceMock.getById(inviteOrganisationResource.getId()))
                .thenReturn(serviceSuccess(inviteOrganisationResource));

        mockMvc.perform(get("/inviteorganisation/{id}", inviteOrganisationResource.getId())
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void getByOrganisationIdWithInvitesForApplication() throws Exception {
        InviteOrganisationResource inviteOrganisationResource = inviteOrganisationResourceBuilder.build();
        long organisationId = 1L;
        long applicationId = 1L;

        when(inviteOrganisationServiceMock.getByOrganisationIdWithInvitesForApplication(organisationId, applicationId))
                .thenReturn(serviceSuccess(inviteOrganisationResource));

        mockMvc.perform(get("/inviteorganisation/organisation/{organisationId}/application/{applicationId}",
                organisationId, applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }
}