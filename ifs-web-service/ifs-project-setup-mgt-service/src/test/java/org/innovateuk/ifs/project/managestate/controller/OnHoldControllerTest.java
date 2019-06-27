package org.innovateuk.ifs.project.managestate.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.managestate.viewmodel.OnHoldViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.service.ProjectStateRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.resource.ProjectState.ON_HOLD;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OnHoldControllerTest extends BaseControllerMockMVCTest<OnHoldController> {

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private ProjectStateRestService projectStateRestService;

    @Override
    protected OnHoldController supplyControllerUnderTest() {
        return new OnHoldController();
    }

    @Test
    public void viewOnHoldStatus() throws Exception {
        long competitionId = 1L;
        long applicationId = 2L;
        long projectId = 123L;
        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withName("Name")
                .withCompetition(competitionId)
                .withApplication(applicationId)
                .withProjectState(ON_HOLD).build();

        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/project/{projectId}/on-hold-status", competitionId, projectId))
                .andExpect(view().name("project/on-hold-status"))
                .andReturn();

        OnHoldViewModel viewModel = (OnHoldViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competitionId, viewModel.getCompetitionId());
        assertEquals(projectId, viewModel.getProjectId());
        assertEquals(applicationId, viewModel.getApplicationId());
        assertEquals("Name", viewModel.getProjectName());
    }

    @Test
    public void viewOnHoldStatus_redirectNotOnHold() throws Exception {
        long competitionId = 1L;
        long applicationId = 2L;
        long projectId = 123L;
        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withName("Name")
                .withCompetition(competitionId)
                .withApplication(applicationId)
                .withProjectState(SETUP).build();

        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));

        mockMvc.perform(get("/competition/{competitionId}/project/{projectId}/on-hold-status", competitionId, projectId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/competition/%d/project/%d/manage-status", competitionId, projectId)));
    }

    @Test
    public void resumeProject() throws Exception {
        long competitionId = 1L;
        long projectId = 123L;

        when(projectStateRestService.resumeProject(projectId)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/project/{projectId}/on-hold-status", competitionId, projectId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/competition/%d/project/%d/manage-status", competitionId, projectId)))
                .andExpect(flash().attribute("resumedFromOnHold", true));

        verify(projectStateRestService).resumeProject(projectId);
    }
}
