package org.innovateuk.ifs.project.invite.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.invite.controller.ProjectPartnerInviteController;
import org.innovateuk.ifs.project.invite.resource.SendProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.transactional.ProjectPartnerInviteService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of project that the project member is being invited to")
                        )
                ));
    }
}
