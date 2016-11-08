package com.worth.ifs.invite.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.invite.builder.ProjectInviteResourceBuilder;
import com.worth.ifs.invite.constant.InviteStatus;
import com.worth.ifs.invite.domain.ProjectInvite;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.PROJECT_INVITE_INVALID;
import static com.worth.ifs.commons.error.CommonFailureKeys.PROJECT_INVITE_INVALID_PROJECT_ID;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InviteProjectControllerTest  extends BaseControllerMockMVCTest<InviteProjectController> {

    @Override
    protected InviteProjectController supplyControllerUnderTest() {
        return new InviteProjectController();
    }

    private InviteProjectResource inviteProjectResource;

    @Before
    public void setUp() {

        inviteProjectResource = ProjectInviteResourceBuilder.newInviteProjectResource().
                withId(1L).
                withEmail("testProject-invite@mail.com").
                withName("test-project-invitece").
                withStatus(InviteStatus.CREATED).
                withOrganisation(25L).
                withProject(2L).
                build();
    }

    @Test
    public void saveProjectInviteWhenErrorWhilstSaving() throws Exception {


        when(inviteProjectServiceMock.saveProjectInvite(inviteProjectResource)).
                thenReturn(serviceFailure(PROJECT_INVITE_INVALID));


        mockMvc.perform(post("/projectinvite/saveInvite")
                .contentType(APPLICATION_JSON)
                .content(toJson(inviteProjectResource)))
                .andExpect(status().isBadRequest());

        verify(inviteProjectServiceMock).saveProjectInvite(inviteProjectResource);
        
    }

    @Test
    public void saveProjectInviteSuccess() throws Exception {

        when(inviteProjectServiceMock.saveProjectInvite(inviteProjectResource)).thenReturn(serviceSuccess());


        mockMvc.perform(post("/projectinvite/saveInvite")
                .contentType(APPLICATION_JSON)
                .content(toJson(inviteProjectResource)))
                .andExpect(status().isOk());

        verify(inviteProjectServiceMock).saveProjectInvite(inviteProjectResource);

    }

    @Test
    public void getProjectInviteByHashWhenProjectInviteNotFound() throws Exception {

        String hash = "has545967h";

        when(inviteProjectServiceMock.getInviteByHash(hash)).thenReturn(serviceFailure(notFoundError(ProjectInvite.class, hash)));

        mockMvc.perform(get("/projectinvite/getProjectInviteByHash/{hash}", hash)).
                andExpect(status().isNotFound());

        verify(inviteProjectServiceMock).getInviteByHash(hash);

    }

    @Test
    public void getProjectInviteByHashSuccess() throws Exception {

        String hash = "has545967h";

        InviteProjectResource inviteProjectResource = ProjectInviteResourceBuilder.newInviteProjectResource().
                withId(1L).
                withEmail("testProject-invite@mail.com").
                withName("test-project-invitece").
                withStatus(InviteStatus.CREATED).
                withOrganisation(25L).
                withProject(2L).
                build();


        when(inviteProjectServiceMock.getInviteByHash(hash)).thenReturn(serviceSuccess(inviteProjectResource));

        mockMvc.perform(get("/projectinvite/getProjectInviteByHash/{hash}", hash)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(inviteProjectResource)));

        verify(inviteProjectServiceMock).getInviteByHash(hash);

    }

    @Test
    public void getInvitesByProjectWhenErrorWhilstRetrieving() throws Exception {

        Long projectId = 123L;

        when(inviteProjectServiceMock.getInvitesByProject(projectId)).
                thenReturn(serviceFailure(new Error(PROJECT_INVITE_INVALID_PROJECT_ID, NOT_FOUND)));

        mockMvc.perform(get("/projectinvite/getInvitesByProjectId/{projectId}", projectId)).
                andExpect(status().isNotFound());

        verify(inviteProjectServiceMock).getInvitesByProject(projectId);
    }

    @Test
    public void getInvitesByProjectSuccess() throws Exception {

        Long projectId = 123L;

        List<InviteProjectResource> inviteProjectResources = ProjectInviteResourceBuilder.newInviteProjectResource().
                withIds(1L).
                withEmails("testProject-invite@mail.com").
                withNames("test-project-invitece").
                withStatuss(InviteStatus.CREATED).
                withOrganisations(25L).
                withProjects(2L).
                build(5);


        when(inviteProjectServiceMock.getInvitesByProject(projectId)).thenReturn(serviceSuccess(inviteProjectResources));

        mockMvc.perform(get("/projectinvite/getInvitesByProjectId/{projectId}", projectId)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(inviteProjectResources)));

        verify(inviteProjectServiceMock).getInvitesByProject(projectId);

    }

    @Test
    public void acceptInviteWhenInvitedEmailNotSameAsUsersEmail() throws Exception {

        String hash = "has545967h";
        Long userId = 123L;

        when(inviteProjectServiceMock.acceptProjectInvite(hash,userId)).
                thenReturn(serviceFailure(new Error("Invited emailaddress not the same as the users emailaddress", HttpStatus.NOT_ACCEPTABLE)));

        mockMvc.perform(put("/projectinvite/acceptInvite/{hash}/{userId}", hash,userId)
                .accept(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());

        verify(inviteProjectServiceMock).acceptProjectInvite(hash, userId);

    }

    @Test
    public void acceptInviteSuccess() throws Exception {

        String hash = "has545967h";
        Long userId = 123L;

        when(inviteProjectServiceMock.acceptProjectInvite(hash,userId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/projectinvite/acceptInvite/{hash}/{userId}", hash,userId)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(inviteProjectServiceMock).acceptProjectInvite(hash, userId);

    }

    @Test
    public void checkExistingUserWhenUserDoesNotExist() throws Exception {

        String hash = "has545967h";

        when(inviteProjectServiceMock.checkUserExistingByInviteHash(hash)).thenReturn(serviceFailure(notFoundError(User.class)));

        mockMvc.perform(get("/projectinvite/checkExistingUser/{hash}", hash)
                .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(inviteProjectServiceMock).checkUserExistingByInviteHash(hash);

    }

    @Test
    public void checkExistingUserSuccess() throws Exception {

        String hash = "has545967h";

        when(inviteProjectServiceMock.checkUserExistingByInviteHash(hash)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/projectinvite/checkExistingUser/{hash}", hash)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(inviteProjectServiceMock).checkUserExistingByInviteHash(hash);

    }
}
