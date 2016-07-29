package com.worth.ifs.project;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.viewmodel.ProjectSpendProfileViewModel;
import org.junit.Test;

import java.time.LocalDate;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectSpendProfileControllerTest extends BaseControllerMockMVCTest<ProjectSpendProfileController> {

    @Override
    protected ProjectSpendProfileController supplyControllerUnderTest() {
        return new ProjectSpendProfileController();
    }

    @Test
    public void viewSpendProfileSuccessfulViewModelPopulation() throws Exception {

        CompetitionResource competitionResource = newCompetitionResource().withName("C1").build();
        ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource projectResource = newProjectResource()
                .withTargetStartDate(LocalDate.now())
                .withDuration(3L)
                .withApplication(applicationResource).build();

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);
        when(applicationService.getById(projectResource.getApplication())).thenReturn(applicationResource);
        when(competitionService.getById(applicationResource.getCompetition())).thenReturn(competitionResource);

        // Assert that the view model is populated with the correct values
        ProjectSpendProfileViewModel viewModel = new ProjectSpendProfileViewModel(projectResource.getId(),
                projectResource.getTargetStartDate(),
                projectResource.getDurationInMonths(),
                competitionResource);

        mockMvc.perform(get("/project/{projectId}/spend-profile", projectResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name("project/spend-profile"));

    }

    @Test
    public void viewSpendProfileWhenProjectDetailsNotInDB() throws Exception {

        ProjectResource projectResource = newProjectResource().build();

        when(projectService.getById(projectResource.getId())).
                thenThrow(new RuntimeException());

        mockMvc.perform(get("/project/{projectId}/spend-profile", projectResource.getId()))
                .andExpect(status().isInternalServerError());

        verify(applicationService, never()).getById(any());
        verify(competitionService, never()).getById(any());

    }
}

