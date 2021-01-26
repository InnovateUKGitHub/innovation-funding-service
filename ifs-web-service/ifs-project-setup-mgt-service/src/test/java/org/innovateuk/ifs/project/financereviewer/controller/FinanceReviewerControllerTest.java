package org.innovateuk.ifs.project.financereviewer.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.financereviewer.service.FinanceReviewerRestService;
import org.innovateuk.ifs.project.financereviewer.viewmodel.FinanceReviewerViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.SimpleUserResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.SimpleUserResourceBuilder.newSimpleUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FinanceReviewerControllerTest extends BaseControllerMockMVCTest<FinanceReviewerController> {

    @Mock
    private FinanceReviewerRestService financeReviewerRestService;

    @Mock
    private ProjectRestService projectRestService;

    @Override
    protected FinanceReviewerController supplyControllerUnderTest() {
        return new FinanceReviewerController();
    }

    @Test
    public void financeReviewer() throws Exception {
        long projectId = 1L;
        long competitionId = 2L;

        List<SimpleUserResource> users = newSimpleUserResource().build(1);
        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withCompetition(competitionId)
                .withName("Project")
                .build();

        when(financeReviewerRestService.findFinanceUsers()).thenReturn(restSuccess(users));
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/project/{projectId}/finance-reviewer", competitionId, projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/finance-reviewer"))
                .andReturn();

        FinanceReviewerViewModel viewModel = (FinanceReviewerViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(viewModel.getCompetitionId(), competitionId);
        assertEquals(viewModel.getProjectId(), projectId);
        assertEquals(viewModel.getProjectName(), "Project");
        assertEquals(viewModel.getUsers(), users);
    }

    @Test
    public void assignFinanceReviewer_success() throws Exception {
        long projectId = 1L;
        long competitionId = 2L;
        long userId = 3L;

        when(financeReviewerRestService.assignFinanceReviewerToProject(userId, projectId)).thenReturn(restSuccess());

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/project/{projectId}/finance-reviewer", competitionId, projectId)
                .param("userId", String.valueOf(userId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/competition/%d/project/%d/details?displayFinanceReviewerSuccess=true", competitionId, projectId)))
                .andReturn();

    }

    @Test
    public void assignFinanceReviewer_missingUserId() throws Exception {
        long projectId = 1L;
        long competitionId = 2L;

        List<SimpleUserResource> users = newSimpleUserResource().build(1);
        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withCompetition(competitionId)
                .withName("Project")
                .build();

        when(financeReviewerRestService.findFinanceUsers()).thenReturn(restSuccess(users));
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));


        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/project/{projectId}/finance-reviewer", competitionId, projectId))
                        .andExpect(status().isOk())
                .andExpect(view().name("project/finance-reviewer"))
                .andExpect(model().attributeHasFieldErrorCode("form", "userId", "NotNull"))
                .andReturn();

        verify(financeReviewerRestService, never()).assignFinanceReviewerToProject(anyLong(), anyLong());
    }
}
