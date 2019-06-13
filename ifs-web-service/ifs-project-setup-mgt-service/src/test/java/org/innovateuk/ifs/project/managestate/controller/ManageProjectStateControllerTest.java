package org.innovateuk.ifs.project.managestate.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.managestate.viewmodel.ManageProjectStateViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.service.ProjectStateRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.resource.ProjectState.HANDLED_OFFLINE;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ManageProjectStateControllerTest extends BaseControllerMockMVCTest<ManageProjectStateController> {

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private ProjectStateRestService projectStateRestService;

    @Override
    protected ManageProjectStateController supplyControllerUnderTest() {
        return new ManageProjectStateController();
    }

    @Test
    public void manageProjectState() throws Exception {
        long competitionId = 1L;
        long projectId = 123L;
        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withName("Name")
                .withCompetition(competitionId)
                .withProjectState(SETUP).build();

        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/project/{projectId}/manage-status", competitionId, projectId))
                .andExpect(view().name("project/manage-project-state"))
                .andReturn();

        ManageProjectStateViewModel viewModel = (ManageProjectStateViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competitionId, viewModel.getCompetitionId());
        assertEquals(projectId, viewModel.getProjectId());
        assertEquals("Name", viewModel.getProjectName());

        assertTrue(viewModel.canHandleOffline());
        assertTrue(viewModel.canWithdraw());
        assertFalse(viewModel.canCompleteOffline());

        assertTrue(viewModel.isInSetup());
        assertFalse(viewModel.isHandledOffline());
        assertFalse(viewModel.isWithdrawn());
        assertFalse(viewModel.isCompleteOffline());
    }

    @Test
    public void setProjectState_handleOffline_success() throws Exception {
        long competitionId = 1L;
        long projectId = 123L;

        when(projectStateRestService.handleProjectOffline(projectId)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/project/{projectId}/manage-status", competitionId, projectId)
                .param("state", HANDLED_OFFLINE.name())
                .param("confirmationOffline", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/competition/%d/project/%d/manage-status", competitionId, projectId)));

        verify(projectStateRestService).handleProjectOffline(projectId);
    }
    @Test
    public void setProjectState_handleOffline_noConfirmation() throws Exception {
        long competitionId = 1L;
        long projectId = 123L;

        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withName("Name")
                .withCompetition(competitionId)
                .withProjectState(SETUP).build();

        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        when(projectStateRestService.handleProjectOffline(projectId)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/project/{projectId}/manage-status", competitionId, projectId)
                .param("state", HANDLED_OFFLINE.name()))
                .andExpect(view().name("project/manage-project-state"))
                .andExpect(model().attributeHasFieldErrorCode("form", "confirmationOffline", "validation.field.must.not.be.blank"));

        verifyZeroInteractions(projectStateRestService);
    }
}
