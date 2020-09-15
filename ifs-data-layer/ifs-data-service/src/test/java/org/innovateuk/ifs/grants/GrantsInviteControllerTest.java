package org.innovateuk.ifs.grants;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.grants.controller.GrantsInviteController;
import org.innovateuk.ifs.grants.transactional.GrantsInviteService;
import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource;
import org.innovateuk.ifs.grantsinvite.resource.SentGrantsInviteResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.UUID;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource.GrantsInviteRole.GRANTS_PROJECT_MANAGER;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GrantsInviteControllerTest extends BaseControllerMockMVCTest<GrantsInviteController> {

    @Override
    protected GrantsInviteController supplyControllerUnderTest() {
        return new GrantsInviteController();
    }

    @Mock
    private GrantsInviteService grantsInviteService;

    @Test
    public void invitePartnerOrganisation() throws Exception {
        long projectId = 1L;

        GrantsInviteResource grantsInviteResource = new GrantsInviteResource(1L, "userName", "email", GRANTS_PROJECT_MANAGER);

        when(grantsInviteService.sendInvite(projectId, grantsInviteResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/grant-invite", projectId, "json")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(grantsInviteResource)))
                .andExpect(status().isOk());

        verify(grantsInviteService, only()).sendInvite(projectId, grantsInviteResource);
    }

    @Test
    public void resendInvite() throws Exception {
        long projectId = 1L;
        long inviteId = 2L;

        when(grantsInviteService.resendInvite(inviteId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/grant-invite/{inviteId}/resend", projectId, inviteId))
                .andExpect(status().isOk());

        verify(grantsInviteService, only()).resendInvite(inviteId);
    }

    @Test
    public void deleteInvite() throws Exception {
        long projectId = 1L;
        long inviteId = 2L;

        when(grantsInviteService.deleteInvite(inviteId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/project/{projectId}/grant-invite/{inviteId}", projectId, inviteId))
                .andExpect(status().isOk());

        verify(grantsInviteService, only()).deleteInvite(inviteId);
    }

    @Test
    public void getInviteByHash() throws Exception {
        long projectId = 1L;
        final String inviteHash = new UUID(1L, 1L).toString();

        SentGrantsInviteResource sentGrantsInviteResource = new SentGrantsInviteResource();

        when(grantsInviteService.getInviteByHash(inviteHash)).thenReturn(serviceSuccess(sentGrantsInviteResource));

        mockMvc.perform(get("/project/{projectId}/grant-invite/{hash}", projectId, inviteHash))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(sentGrantsInviteResource)));

        verify(grantsInviteService, only()).getInviteByHash(inviteHash);
    }

    @Test
    public void acceptInvite() throws Exception {
        long projectId = 1L;
        long inviteId = 2L;
        long organisationId = 3L;

        when(grantsInviteService.acceptInvite(inviteId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/grant-invite/{inviteId}/accept", projectId, inviteId, organisationId))
                .andExpect(status().isOk());

        verify(grantsInviteService, only()).acceptInvite(inviteId);
    }
}
