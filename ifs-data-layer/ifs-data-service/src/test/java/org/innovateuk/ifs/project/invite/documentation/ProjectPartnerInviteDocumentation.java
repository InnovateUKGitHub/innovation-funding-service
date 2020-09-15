package org.innovateuk.ifs.project.invite.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.invite.controller.ProjectPartnerInviteController;
import org.innovateuk.ifs.project.invite.resource.SendProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.transactional.ProjectPartnerInviteService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.invite.builder.SentProjectPartnerInviteResourceBuilder.newSentProjectPartnerInviteResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectPartnerInviteDocumentation extends BaseControllerMockMVCTest<ProjectPartnerInviteController> {

    @Mock
    private ProjectPartnerInviteService projectPartnerInviteService;

    @Override
    protected ProjectPartnerInviteController supplyControllerUnderTest() {
        return new ProjectPartnerInviteController();
    }

    private static FieldDescriptor[] sentResourceDocs = {
                fieldWithPath("id").description("Id of the invite"),
                fieldWithPath("sentOn").description("Date the invite was sent"),
                fieldWithPath("email").description("Email address that invite was sent to."),
                fieldWithPath("organisationName").description("Organisation name for invite"),
                fieldWithPath("userName").description("Users name of invite"),
                fieldWithPath("user").optional().description("The id of the user the invite was sent to"),
                fieldWithPath("status").description("The status of the invite"),
                fieldWithPath("projectName").description("The name of the project the invite is to"),
                fieldWithPath("applicationId").description("Id of the application"),
                fieldWithPath("competitionId").description("Id of the competition")
    };

    @Test
    public void invitePartnerOrganisation() throws Exception {
        long projectId = 123L;
        SendProjectPartnerInviteResource invite = new SendProjectPartnerInviteResource("asd", "asd", "asd");
        when(projectPartnerInviteService.invitePartnerOrganisation(projectId, invite)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/project-partner-invite", projectId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(toJson(invite)))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of project that the project member is being invited to")
                        )
                ));
    }

    @Test
    public void getPartnerInvites() throws Exception {
        long projectId = 123L;
        List<SentProjectPartnerInviteResource> invites = newSentProjectPartnerInviteResource().build(1);
        when(projectPartnerInviteService.getPartnerInvites(projectId)).thenReturn(serviceSuccess(invites));
        mockMvc.perform(get("/project/{projectId}/project-partner-invite", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of project to get invites from")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("An array of invites")
                        ).andWithPrefix("[].", sentResourceDocs)
                ));
    }

    @Test
    public void resendInvite() throws Exception {
        long projectId = 123L;
        long inviteId = 321L;
        when(projectPartnerInviteService.resendInvite(inviteId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/project-partner-invite/{inviteId}/resend", projectId, inviteId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project the invite belongs to"),
                                parameterWithName("inviteId").description("Id of the invite")
                        )
                ));
    }

    @Test
    public void deleteInvite() throws Exception {
        long projectId = 123L;
        long inviteId = 321L;
        when(projectPartnerInviteService.deleteInvite(inviteId)).thenReturn(serviceSuccess());
        mockMvc.perform(delete("/project/{projectId}/project-partner-invite/{inviteId}", projectId, inviteId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isNoContent())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project the invite belongs to"),
                                parameterWithName("inviteId").description("Id of the invite")
                        )
                ));
    }

    @Test
    public void getInviteByHash() throws Exception {
        long projectId = 123L;
        String hash = "hash";
        when(projectPartnerInviteService.getInviteByHash(hash)).thenReturn(serviceSuccess(newSentProjectPartnerInviteResource().build()));
        mockMvc.perform(get("/project/{projectId}/project-partner-invite/{hash}", projectId, hash)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project the invite belongs to"),
                                parameterWithName("hash").description("Hash of the invite")
                        ),
                        responseFields(
                                sentResourceDocs
                        )
                ));
    }

    @Test
    public void acceptInvite() throws Exception {
        long projectId = 123L;
        long inviteId = 321L;;
        long organisationId = 321L;
        when(projectPartnerInviteService.acceptInvite(inviteId, organisationId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/project-partner-invite/{inviteId}/organisation/{organisationId}/accept", projectId, inviteId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project the invite belongs to"),
                                parameterWithName("inviteId").description("Id of the invite"),
                                parameterWithName("organisationId").description("organisation id to join the project")
                        )
                ));
    }
}
