package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.InviteProjectDocs.inviteProjectFields;
import static org.innovateuk.ifs.documentation.InviteProjectDocs.inviteProjectResourceBuilder;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InviteProjectControllerDocumentation extends BaseControllerMockMVCTest<InviteProjectController> {

    @Override
    protected InviteProjectController supplyControllerUnderTest() {
        return new InviteProjectController();
    }

    @Test
    public void saveProjectInvite() throws Exception {

        InviteProjectResource inviteProject = inviteProjectResourceBuilder.build();

        when(projectInviteServiceMock.saveProjectInvite(inviteProject)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/projectinvite/saveInvite")
                .contentType(APPLICATION_JSON)
                .content(toJson(inviteProject)))
                .andExpect(status().isOk());

    }

    @Test
    public void getByHash() throws Exception {

        InviteProjectResource inviteProject = inviteProjectResourceBuilder.build();

        when(projectInviteServiceMock.getInviteByHash(inviteProject.getHash())).thenReturn(serviceSuccess(inviteProject));

        mockMvc.perform(get("/projectinvite/getProjectInviteByHash/{hash}", inviteProject.getHash())).
                andExpect(status().isOk()).
                andDo(document("invite-project/{method-name}",
                        pathParameters(
                                parameterWithName("hash").description("Hash of the invite that is being retrieved")
                        ),
                        responseFields(inviteProjectFields)
                ));

    }

    @Test
    public void getInvitesByProject() throws Exception {

        Long projectId = 123L;

        List<InviteProjectResource> inviteProjects = inviteProjectResourceBuilder.build(2);

        when(projectInviteServiceMock.getInvitesByProject(projectId)).thenReturn(serviceSuccess(inviteProjects));

        mockMvc.perform(get("/projectinvite/getInvitesByProjectId/{projectId}", projectId)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(inviteProjects)));
    }

    @Test
    public void acceptInvite() throws Exception {

        String hash = "has545967h";
        Long userId = 123L;

        when(projectInviteServiceMock.acceptProjectInvite(hash, userId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/projectinvite/acceptInvite/{hash}/{userId}", hash,userId)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void checkExistingUser() throws Exception {

        String hash = "has545967h";

        when(projectInviteServiceMock.checkUserExistsForInvite(hash)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/projectinvite/checkExistingUser/{hash}", hash)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
