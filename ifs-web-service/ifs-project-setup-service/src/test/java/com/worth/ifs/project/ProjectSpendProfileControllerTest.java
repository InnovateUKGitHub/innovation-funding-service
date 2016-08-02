package com.worth.ifs.project;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.viewmodel.ProjectSpendProfileViewModel;
import org.junit.Test;

import java.time.LocalDate;

import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
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

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.now())
                .withDuration(3L)
                .build();

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        // Assert that the view model is populated with the correct values
        ProjectSpendProfileViewModel viewModel = new ProjectSpendProfileViewModel(projectResource);

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
                .andExpect(status().isInternalServerError())
                .andExpect(model().attributeDoesNotExist("model"));
    }
}
