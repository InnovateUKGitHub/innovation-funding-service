package com.worth.ifs.project;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.LongStream;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.viewmodel.ProjectSpendProfileViewModel;
import com.worth.ifs.project.viewmodel.SpendProfileSummaryModel;
import com.worth.ifs.project.viewmodel.SpendProfileSummaryYearModel;

import org.junit.Test;

import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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

        List<SpendProfileSummaryYearModel> years = createSpendProfileSummaryYears(projectResource);
        SpendProfileSummaryModel summary = new SpendProfileSummaryModel(years);

        // Assert that the view model is populated with the correct values
        ProjectSpendProfileViewModel viewModel = new ProjectSpendProfileViewModel(projectResource, summary);

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

    private List<SpendProfileSummaryYearModel> createSpendProfileSummaryYears(ProjectResource project){
        Integer startYear = project.getTargetStartDate().getYear();
        Integer endYear = project.getTargetStartDate().plusMonths(project.getDurationInMonths()).getYear()+1;
        //TODO add logic for populating the table with the correct values after this has been implemented
        return LongStream.range(startYear, endYear).mapToObj(year -> new SpendProfileSummaryYearModel(year, "123456.78")).collect(toList());
    }
}
