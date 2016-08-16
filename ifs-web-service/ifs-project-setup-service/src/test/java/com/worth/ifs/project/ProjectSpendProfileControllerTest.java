package com.worth.ifs.project;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.error.exception.ObjectNotFoundException;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.project.viewmodel.ProjectSpendProfileViewModel;
import com.worth.ifs.project.viewmodel.SpendProfileSummaryModel;
import com.worth.ifs.project.viewmodel.SpendProfileSummaryYearModel;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.LongStream;

import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectSpendProfileControllerTest extends BaseControllerMockMVCTest<ProjectSpendProfileController> {

    @Override
    protected ProjectSpendProfileController supplyControllerUnderTest() {
        return new ProjectSpendProfileController();
    }

    @Test
    public void viewSpendProfileWhenProjectDetailsNotInDB() throws Exception {

        Long organisationId = 1L;

        ProjectResource projectResource = newProjectResource().build();

        when(projectService.getById(projectResource.getId())).
                thenThrow(new ObjectNotFoundException("Project not found", null));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectResource.getId(), organisationId))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeDoesNotExist("model"));

        verify(projectFinanceService, never()).getSpendProfileTable(projectResource.getId(), organisationId);
    }

    @Test
    public void viewSpendProfileWhenSpendProfileDetailsNotInDB() throws Exception {

        Long organisationId = 1L;

        ProjectResource projectResource = newProjectResource().build();

        when(projectService.getById(projectResource.getId())).
                thenReturn(projectResource);

        when(projectFinanceService.getSpendProfileTable(projectResource.getId(), organisationId)).
                thenThrow(new ObjectNotFoundException("SpendProfile not found", null));


        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectResource.getId(), organisationId))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeDoesNotExist("model"));
    }

    @Test
    public void viewSpendProfileSuccessfulViewModelPopulation() throws Exception {

        Long organisationId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 03, 01))
                .withDuration(3L)
                .build();

        SpendProfileTableResource spendProfileTable = new SpendProfileTableResource();

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        when(projectFinanceService.getSpendProfileTable(projectResource.getId(), organisationId)).thenReturn(spendProfileTable);

        List<SpendProfileSummaryYearModel> years = createSpendProfileSummaryYears(projectResource);
        SpendProfileSummaryModel summary = new SpendProfileSummaryModel(years);

        // Assert that the view model is populated with the correct values
        ProjectSpendProfileViewModel expectedViewModel = new ProjectSpendProfileViewModel(projectResource, spendProfileTable, summary);

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectResource.getId(), organisationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("project/spend-profile"));

    }

    private List<SpendProfileSummaryYearModel> createSpendProfileSummaryYears(ProjectResource project){
        Integer startYear = project.getTargetStartDate().getYear();
        Integer endYear = project.getTargetStartDate().plusMonths(project.getDurationInMonths()).getYear()+1;
        //TODO add logic for populating the table with the correct values after this has been implemented
        return LongStream.range(startYear, endYear).mapToObj(year -> new SpendProfileSummaryYearModel(year, "123456.78")).collect(toList());
    }
}
