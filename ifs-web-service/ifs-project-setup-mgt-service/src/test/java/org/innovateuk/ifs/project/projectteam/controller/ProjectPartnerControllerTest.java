package org.innovateuk.ifs.project.projectteam.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.invite.resource.ProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.service.ProjectPartnerInviteRestService;
import org.innovateuk.ifs.project.projectteam.viewmodel.ProjectPartnerInviteViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectPartnerControllerTest extends BaseControllerMockMVCTest<ProjectPartnerController> {

    @Override
    protected ProjectPartnerController supplyControllerUnderTest() {
        return new ProjectPartnerController();
    }

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private ProjectPartnerInviteRestService projectPartnerInviteRestService;

    @Test
    public void inviteProjectPartnerForm() throws Exception {
        long projectId = 999L;
        long competitionId = 888L;
        long applicationId = 444L;
        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withCompetition(competitionId)
                .withApplication(applicationId)
                .withName("project")
                .build();
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        ProjectPartnerInviteViewModel expected = new ProjectPartnerInviteViewModel(project);

        MvcResult result = mockMvc.perform(get("/competition/{compId}/project/{projectId}/team/partner", competitionId, projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/project-partner-invite"))
                .andReturn();

        ProjectPartnerInviteViewModel actual = (ProjectPartnerInviteViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(expected, actual);
    }

    @Test
    public void inviteProjectPartner() throws Exception {
        long projectId = 999L;
        long competitionId = 888L;

        ProjectPartnerInviteResource invite = new ProjectPartnerInviteResource("org", "name", "blah@gmail.com");

        when(projectPartnerInviteRestService.invitePartnerOrganisation(projectId, invite)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{compId}/project/{projectId}/team/partner", competitionId, projectId)
                .param("organisationName", "org")
                .param("userName", "name")
                .param("email", "blah@gmail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/888/project/999/team"));

        verify(projectPartnerInviteRestService).invitePartnerOrganisation(projectId, invite);
    }
}
