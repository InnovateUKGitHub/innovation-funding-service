package org.innovateuk.ifs.project.projectteam.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.invite.service.ProjectInviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.projectteam.ProjectTeamRestService;
import org.innovateuk.ifs.project.projectteam.populator.ProjectTeamViewModelPopulator;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.projectteam.util.ProjectInviteHelper;
import org.innovateuk.ifs.projectteam.viewmodel.ProjectTeamViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder.newProjectUserInviteResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectTeamControllerTest extends BaseControllerMockMVCTest<ProjectTeamController> {

    @Override
    protected ProjectTeamController supplyControllerUnderTest() {
        return new ProjectTeamController();
    }

    @Mock
    private ProjectTeamViewModelPopulator populator;

    @Mock
    private ProjectInviteRestService projectInviteRestService;

    @Mock
    private ProjectService projectService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ProjectTeamRestService projectTeamRestService;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Spy
    @InjectMocks
    private ProjectInviteHelper projectInviteHelper;

    @Test
    public void viewProjectTeam() throws Exception {
        UserResource loggedInUser = newUserResource().build();
        setLoggedInUser(loggedInUser);
        long projectId = 999L;
        ProjectTeamViewModel expected = mock(ProjectTeamViewModel.class);

        when(populator.populate(projectId, loggedInUser)).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/project/{id}/team", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("projectteam/project-team"))
                .andExpect(model().attributeDoesNotExist("internalUserView"))
                .andReturn();

        ProjectTeamViewModel actual = (ProjectTeamViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(expected, actual);
    }

    @Test
    public void openAddTeamMemberForm() throws Exception {
        UserResource loggedInUser = newUserResource().build();
        setLoggedInUser(loggedInUser);
        long projectId = 999L;
        long organisationId = 3L;
        ProjectTeamViewModel expected = mock(ProjectTeamViewModel.class);

        when(populator.populate(projectId, loggedInUser)).thenReturn(expected);
        when(expected.openAddTeamMemberForm(organisationId)).thenReturn(expected);

        MvcResult result = mockMvc.perform(post("/project/{id}/team", projectId)
                .param("add-team-member", String.valueOf(organisationId)))
                .andExpect(status().isOk())
                .andExpect(view().name("projectteam/project-team"))
                .andExpect(model().attributeDoesNotExist("internalUserView"))
                .andReturn();

        ProjectTeamViewModel actual = (ProjectTeamViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expected, actual);
        verify(expected).openAddTeamMemberForm(organisationId);
    }

    @Test
    public void closeAddTeamMemberForm() throws Exception {
        long projectId = 999L;

        mockMvc.perform(post("/project/{id}/team", projectId)
                .param("close-add-team-member-form", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/project/%d/team", projectId)))
                .andReturn();
    }

    @Test
    public void inviteToProject() throws Exception {
        UserResource loggedInUser = newUserResource().build();
        setLoggedInUser(loggedInUser);
        long projectId = 999L;
        ProjectTeamViewModel expected = mock(ProjectTeamViewModel.class);
        String email = "someone@gmail.com";
        String userName = "Some One";
        ProjectResource projectResource = newProjectResource()
                .withId(projectId)
                .withApplication(5L)
                .build();
        OrganisationResource leadOrganisation = newOrganisationResource().build();
        OrganisationResource organisationResource = newOrganisationResource().build();
        ProjectUserInviteResource projectUserInviteResource = new ProjectUserInviteResource(userName, email, projectId);
        projectUserInviteResource.setOrganisation(organisationResource.getId());
        projectUserInviteResource.setApplicationId(projectResource.getApplication());
        projectUserInviteResource.setLeadOrganisationId(leadOrganisation.getId());
        projectUserInviteResource.setOrganisationName(organisationResource.getName());

        when(expected.openAddTeamMemberForm(organisationResource.getId())).thenReturn(expected);
        when(populator.populate(projectId, loggedInUser)).thenReturn(expected);
        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(projectService.getLeadOrganisation(projectId)).thenReturn(leadOrganisation);
        when(organisationRestService.getOrganisationById(organisationResource.getId())).thenReturn(restSuccess(organisationResource));
        when(projectInviteRestService.getInvitesByProject(projectId)).thenReturn(restSuccess(asList(projectUserInviteResource)));
        when(projectTeamRestService.inviteProjectMember(projectId, projectUserInviteResource)).thenReturn(restSuccess());

        MvcResult result = mockMvc.perform(post("/project/{id}/team", projectId)
                .param("invite-to-project", String.valueOf(organisationResource.getId()))
                .param("name", userName)
                .param("email", email))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/project/%d/team", projectId)))
                .andReturn();

        verify(projectTeamRestService).inviteProjectMember(projectId, projectUserInviteResource);
    }

    @Test
    public void resendInvite() throws Exception {
        long projectId = 4L;
        long organisationId = 21L;
        long inviteId = 3L;

        String invitedUserName = "test";
        String invitedUserEmail = "test@test.com";

        OrganisationResource leadOrganisation = newOrganisationResource().withName("Lead Organisation").build();

        List<ProjectUserInviteResource> existingInvites = newProjectUserInviteResource().withId(inviteId)
                .withProject(projectId).withName("exist test", invitedUserName)
                .withEmail("existing@test.com", invitedUserEmail)
                .withOrganisation(organisationId)
                .withStatus(SENT)
                .withLeadOrganisation(leadOrganisation.getId()).build(1);

        when(projectInviteRestService.getInvitesByProject(projectId)).thenReturn(restSuccess(existingInvites));
        when(projectTeamRestService.inviteProjectMember(projectId, existingInvites.get(0))).thenReturn(restSuccess());

        mockMvc.perform(post("/project/{id}/team", projectId)
                .param("resend-invite", "3"))
                .andExpect(status().is3xxRedirection());

        verify(projectTeamRestService).inviteProjectMember(projectId, existingInvites.get(0));
        verify(cookieFlashMessageFilter).setFlashMessage(any(), eq("emailSent"));

    }

    @Test
    public void removeUser() throws Exception {
        UserResource loggedInUser = newUserResource().build();
        setLoggedInUser(loggedInUser);
        long userId = 444L;
        long projectId = 555L;

        when(projectTeamRestService.removeUser(projectId, userId)).thenReturn(restSuccess());

        mockMvc.perform(post("/project/" + projectId + "/team")
                .param("remove-team-member", String.valueOf(userId)))
                .andExpect(status().is3xxRedirection());

        verify(projectTeamRestService).removeUser(projectId, userId);
    }

    @Test
    public void removeInvite() throws Exception {

        long inviteId = 777L;
        long projectId = 888L;

        when(projectTeamRestService.removeInvite(projectId, inviteId)).thenReturn(restSuccess());

        mockMvc.perform(post("/project/" + projectId + "/team")
                                .param("remove-invite", String.valueOf(inviteId)))
                .andExpect(status().is3xxRedirection());

        verify(projectTeamRestService).removeInvite(projectId, inviteId);
    }
}
