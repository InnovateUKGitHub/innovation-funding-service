package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.invite.resource.ProjectInviteResource;
import org.innovateuk.ifs.invite.transactional.ProjectInviteService;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_INVITE_INVALID;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_INVITE_INVALID_PROJECT_ID;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ProjectInviteResourceBuilder.newProjectInviteResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectInviteControllerTest  extends BaseControllerMockMVCTest<ProjectInviteController> {

    @Mock
    private ProjectInviteService projectInviteServiceMock;

    @Override
    protected ProjectInviteController supplyControllerUnderTest() {
        return new ProjectInviteController();
    }

    private ProjectInviteResource projectInviteResource;

    @Before
    public void setUp() {

        projectInviteResource = newProjectInviteResource().
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


        when(projectInviteServiceMock.saveProjectInvite(projectInviteResource)).
                thenReturn(serviceFailure(PROJECT_INVITE_INVALID));


        mockMvc.perform(post("/projectinvite/saveInvite")
                .contentType(APPLICATION_JSON)
                .content(toJson(projectInviteResource)))
                .andExpect(status().isBadRequest());

        verify(projectInviteServiceMock).saveProjectInvite(projectInviteResource);
        
    }

    @Test
    public void saveProjectInviteSuccess() throws Exception {

        when(projectInviteServiceMock.saveProjectInvite(projectInviteResource)).thenReturn(serviceSuccess());


        mockMvc.perform(post("/projectinvite/saveInvite")
                .contentType(APPLICATION_JSON)
                .content(toJson(projectInviteResource)))
                .andExpect(status().isOk());

        verify(projectInviteServiceMock).saveProjectInvite(projectInviteResource);

    }

    @Test
    public void getProjectInviteByHashWhenProjectInviteNotFound() throws Exception {

        String hash = "has545967h";

        when(projectInviteServiceMock.getInviteByHash(hash)).thenReturn(serviceFailure(notFoundError(ProjectInvite.class, hash)));

        mockMvc.perform(get("/projectinvite/getProjectInviteByHash/{hash}", hash)).
                andExpect(status().isNotFound());

        verify(projectInviteServiceMock).getInviteByHash(hash);

    }

    @Test
    public void getProjectInviteByHashSuccess() throws Exception {

        String hash = "has545967h";

        ProjectInviteResource projectInviteResource = newProjectInviteResource().
                withId(1L).
                withEmail("testProject-invite@mail.com").
                withName("test-project-invitece").
                withStatus(InviteStatus.CREATED).
                withOrganisation(25L).
                withProject(2L).
                build();


        when(projectInviteServiceMock.getInviteByHash(hash)).thenReturn(serviceSuccess(projectInviteResource));

        mockMvc.perform(get("/projectinvite/getProjectInviteByHash/{hash}", hash)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(projectInviteResource)));

        verify(projectInviteServiceMock).getInviteByHash(hash);

    }

    @Test
    public void getInvitesByProjectWhenErrorWhilstRetrieving() throws Exception {

        Long projectId = 123L;

        when(projectInviteServiceMock.getInvitesByProject(projectId)).
                thenReturn(serviceFailure(new Error(PROJECT_INVITE_INVALID_PROJECT_ID, NOT_FOUND)));

        mockMvc.perform(get("/projectinvite/getInvitesByProjectId/{projectId}", projectId)).
                andExpect(status().isNotFound());

        verify(projectInviteServiceMock).getInvitesByProject(projectId);
    }

    @Test
    public void getInvitesByProjectSuccess() throws Exception {

        Long projectId = 123L;

        List<ProjectInviteResource> projectInviteResources = newProjectInviteResource().
                withId(1L).
                withEmail("testProject-invite@mail.com").
                withName("test-project-invitece").
                withStatus(InviteStatus.CREATED).
                withOrganisation(25L).
                withProject(2L).
                build(5);


        when(projectInviteServiceMock.getInvitesByProject(projectId)).thenReturn(serviceSuccess(projectInviteResources));

        mockMvc.perform(get("/projectinvite/getInvitesByProjectId/{projectId}", projectId)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(projectInviteResources)));

        verify(projectInviteServiceMock).getInvitesByProject(projectId);

    }

    @Test
    public void acceptInviteWhenInvitedEmailNotSameAsUsersEmail() throws Exception {

        String hash = "has545967h";
        Long userId = 123L;

        when(projectInviteServiceMock.acceptProjectInvite(hash, userId)).
                thenReturn(serviceFailure(new Error("Invited emailaddress not the same as the users emailaddress", HttpStatus.NOT_ACCEPTABLE)));

        mockMvc.perform(put("/projectinvite/acceptInvite/{hash}/{userId}", hash,userId)
                .accept(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());

        verify(projectInviteServiceMock).acceptProjectInvite(hash, userId);

    }

    @Test
    public void acceptInviteSuccess() throws Exception {

        String hash = "has545967h";
        Long userId = 123L;

        when(projectInviteServiceMock.acceptProjectInvite(hash, userId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/projectinvite/acceptInvite/{hash}/{userId}", hash,userId)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(projectInviteServiceMock).acceptProjectInvite(hash, userId);

    }

    @Test
    public void checkExistingUserWhenUserDoesNotExist() throws Exception {

        String hash = "has545967h";

        when(projectInviteServiceMock.checkUserExistsForInvite(hash)).thenReturn(serviceFailure(notFoundError(User.class)));

        mockMvc.perform(get("/projectinvite/checkExistingUser/{hash}", hash)
                .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(projectInviteServiceMock).checkUserExistsForInvite(hash);

    }

    @Test
    public void checkExistingUserSuccess() throws Exception {

        String hash = "has545967h";

        when(projectInviteServiceMock.checkUserExistsForInvite(hash)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/projectinvite/checkExistingUser/{hash}", hash)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(projectInviteServiceMock).checkUserExistsForInvite(hash);

    }
}
