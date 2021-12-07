package org.innovateuk.ifs.project.setupcomplete.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.service.ProjectStateRestService;
import org.innovateuk.ifs.project.setupcomplete.viewmodel.ProjectSetupCompleteViewModel;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectSetupCompleteControllerTest extends BaseControllerMockMVCTest<ProjectSetupCompleteController> {

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private ProjectStateRestService projectStateRestService;

    @Mock
    private CompetitionRestService competitionRestService;



    private static final long competitionId = 1L;
    private static final long projectId = 2L;

    private static final LocalDate projectStartDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);

    private static final ProjectResource project = newProjectResource()
            .withId(projectId)
            .withApplication(3L)
            .withCompetition(competitionId)
            .withName("Project name")
            .withProjectState(ProjectState.LIVE)
            .withTargetStartDate(projectStartDate)
            .build();

    private static final CompetitionResource competition = newCompetitionResource()
            .withId(competitionId)
            .build();

    @Override
    protected ProjectSetupCompleteController supplyControllerUnderTest() {
        return new ProjectSetupCompleteController();
    }

    @Test
    public void viewSetupCompletePage() throws Exception {
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/project/{projectId}/setup-complete", competitionId, projectId))
                .andExpect(view().name("project/setup-complete"))
                .andReturn();

        ProjectSetupCompleteViewModel viewModel = (ProjectSetupCompleteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(viewModel.getProjectId(), projectId);
        assertEquals(viewModel.getApplicationId(), 3L);
        assertEquals(viewModel.getCompetitionId(), competitionId);
        assertEquals(viewModel.getProjectName(), "Project name");
        assertEquals(viewModel.getState(), ProjectState.LIVE);
        assertEquals(viewModel.getTargetStartDate(), projectStartDate);
        assertTrue(viewModel.isReadonly());
    }

    @Test
    public void saveProjectState_success() throws Exception {
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(projectStateRestService.markAsSuccessful(projectId)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/project/{projectId}/setup-complete", competitionId, projectId)
                .param("successful", "true")
                .param("successfulConfirmation", "true"))
                .andExpect(redirectedUrl(String.format("/competition/%d/project/%d/setup-complete", competitionId, projectId)));

        verify(projectStateRestService).markAsSuccessful(projectId);
    }

    @Test
    public void saveProjectStateLoan_success() throws Exception {
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(projectStateRestService.markAsSuccessful(projectId, projectStartDate)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/project/{projectId}/loan-setup-complete", competitionId, projectId)
                .param("successful", "true")
                .param("successfulConfirmation", "true")
                .param("startDateYear", String.valueOf(projectStartDate.getYear()))
                .param("startDateMonth", String.valueOf(projectStartDate.getMonthValue()))
                .param("startDateDay", String.valueOf(projectStartDate.getDayOfMonth())))
                .andExpect(redirectedUrl(String.format("/competition/%d/project/%d/setup-complete", competitionId, projectId)));

        verify(projectStateRestService).markAsSuccessful(projectId, projectStartDate);
    }

    @Test
    public void saveProjectState_validation() throws Exception {
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        mockMvc.perform(post("/competition/{competitionId}/project/{projectId}/setup-complete", competitionId, projectId)
                .param("successful", "true"))
                .andExpect(view().name("project/setup-complete"))
                .andExpect(model().attributeHasFieldErrorCode("form", "successfulConfirmation", "validation.field.must.not.be.blank"));

        verifyZeroInteractions(projectStateRestService);
    }
}
