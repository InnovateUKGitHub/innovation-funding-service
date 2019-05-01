package org.innovateuk.ifs.project.projectteam.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.projectteam.ProjectTeamRestService;
import org.innovateuk.ifs.project.projectteam.populator.ProjectTeamViewModelPopulator;
import org.innovateuk.ifs.project.projectteam.viewmodel.ProjectTeamViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    private ProjectDetailsService projectDetailsService;
    @Mock
    private ProjectService projectService;
    @Mock
    private OrganisationRestService organisationRestService;
    @Mock
    private ProjectTeamRestService projectTeamRestService;

    @Test
    public void viewProjectTeam() throws Exception {
        UserResource loggedInUser = newUserResource().build();
        setLoggedInUser(loggedInUser);
        long projectId = 999L;
        ProjectTeamViewModel expected = mock(ProjectTeamViewModel.class);

        when(populator.populate(projectId, loggedInUser)).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/project/{id}/team", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/project-team"))
                .andExpect(model().attributeDoesNotExist("readOnlyView"))
                .andReturn();

        ProjectTeamViewModel actual = (ProjectTeamViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(expected, actual);
    }

    @Test
    public void addTeamMember() throws Exception {
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
                .andExpect(view().name("project/project-team"))
                .andExpect(model().attributeDoesNotExist("readOnlyView"))
                .andReturn();

        ProjectTeamViewModel actual = (ProjectTeamViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expected, actual);
        verify(expected).openAddTeamMemberForm(organisationId);
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
        when(projectDetailsService.saveProjectInvite(projectUserInviteResource)).thenReturn(serviceSuccess());
        when(projectDetailsService.getInvitesByProject(projectId)).thenReturn(serviceSuccess(asList(projectUserInviteResource)));
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

}
