package org.innovateuk.ifs.project.invite.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.invite.controller.ProjectPartnerInviteController;
import org.innovateuk.ifs.project.invite.resource.SendProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.transactional.ProjectPartnerInviteService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.invite.builder.SentProjectPartnerInviteResourceBuilder.newSentProjectPartnerInviteResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectPartnerInviteDocumentation extends BaseControllerMockMVCTest<ProjectPartnerInviteController> {

    @Mock
    private ProjectPartnerInviteService projectPartnerInviteService;

    @Override
    protected ProjectPartnerInviteController supplyControllerUnderTest() {
        return new ProjectPartnerInviteController();
    }

    @Test
    public void invitePartnerOrganisation() throws Exception {
        long projectId = 123L;
        SendProjectPartnerInviteResource invite = new SendProjectPartnerInviteResource("asd", "asd", "asd");
        when(projectPartnerInviteService.invitePartnerOrganisation(projectId, invite)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/project-partner-invite", projectId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(toJson(invite)))
                .andExpect(status().isOk());
    }

    @Test
    public void getPartnerInvites() throws Exception {
        long projectId = 123L;
        List<SentProjectPartnerInviteResource> invites = newSentProjectPartnerInviteResource().build(1);
        when(projectPartnerInviteService.getPartnerInvites(projectId)).thenReturn(serviceSuccess(invites));
        mockMvc.perform(get("/project/{projectId}/project-partner-invite", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void resendInvite() throws Exception {
        long projectId = 123L;
        long inviteId = 321L;
        when(projectPartnerInviteService.resendInvite(inviteId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/project-partner-invite/{inviteId}/resend", projectId, inviteId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteInvite() throws Exception {
        long projectId = 123L;
        long inviteId = 321L;
        when(projectPartnerInviteService.deleteInvite(inviteId)).thenReturn(serviceSuccess());
        mockMvc.perform(delete("/project/{projectId}/project-partner-invite/{inviteId}", projectId, inviteId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getInviteByHash() throws Exception {
        long projectId = 123L;
        String hash = "hash";
        when(projectPartnerInviteService.getInviteByHash(hash)).thenReturn(serviceSuccess(newSentProjectPartnerInviteResource().build()));
        mockMvc.perform(get("/project/{projectId}/project-partner-invite/{hash}", projectId, hash)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void acceptInvite() throws Exception {
        long projectId = 123L;
        long inviteId = 321L;
        long organisationId = 321L;
        when(projectPartnerInviteService.acceptInvite(inviteId, organisationId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/project-partner-invite/{inviteId}/organisation/{organisationId}/accept", projectId, inviteId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }
}
