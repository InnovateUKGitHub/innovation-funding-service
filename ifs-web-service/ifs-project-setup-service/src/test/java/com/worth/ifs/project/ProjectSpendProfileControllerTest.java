package com.worth.ifs.project;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.exception.ObjectNotFoundException;
import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.project.form.SpendProfileForm;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.project.validation.SpendProfileCostValidator;
import com.worth.ifs.project.viewmodel.ProjectSpendProfileViewModel;
import com.worth.ifs.project.viewmodel.SpendProfileSummaryModel;
import com.worth.ifs.project.viewmodel.SpendProfileSummaryYearModel;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.ObjectError;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.worth.ifs.commons.error.CommonFailureKeys.SPEND_PROFILE_CANNOT_MARK_AS_COMPLETE_BECAUSE_SPEND_HIGHER_THAN_ELIGIBLE;
import static com.worth.ifs.commons.error.CommonFailureKeys.SPEND_PROFILE_CONTAINS_FRACTIONS_IN_COST_FOR_SPECIFIED_CATEGORY_AND_MONTH;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectSpendProfileControllerTest extends BaseControllerMockMVCTest<ProjectSpendProfileController> {

    @Mock
    public SpendProfileCostValidator spendProfileCostValidator;

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
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .build();

        SpendProfileTableResource expectedTable = buildSpendProfileTableResource(projectResource);

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        when(projectFinanceService.getSpendProfileTable(projectResource.getId(), organisationId)).thenReturn(expectedTable);

        ProjectSpendProfileViewModel expectedViewModel = buildExpectedProjectSpendProfileViewModel(organisationId, projectResource, expectedTable);

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectResource.getId(), organisationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("project/spend-profile"));

    }

    @Test
    public void saveSpendProfileWhenErrorWhilstSaving() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        SpendProfileTableResource table = new SpendProfileTableResource();

        when(projectFinanceService.getSpendProfileTable(projectId, organisationId)).thenReturn(table);

        List<Error> incorrectCosts = new ArrayList<>();
        incorrectCosts.add(new Error(SPEND_PROFILE_CONTAINS_FRACTIONS_IN_COST_FOR_SPECIFIED_CATEGORY_AND_MONTH, asList("Labour", 1), HttpStatus.BAD_REQUEST));

        when(projectFinanceService.saveSpendProfile(projectId, organisationId, table)).thenReturn(serviceFailure(incorrectCosts));

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/edit", projectId, organisationId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("table.markedAsComplete", "true")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/project/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile/edit"));
    }

    @Test
    public void saveSpendProfileSuccess() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        SpendProfileTableResource table = new SpendProfileTableResource();

        when(projectFinanceService.getSpendProfileTable(projectId, organisationId)).thenReturn(table);

        when(projectFinanceService.saveSpendProfile(projectId, organisationId, table)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/edit", projectId, organisationId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("table.markedAsComplete", "true")
        )
        .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/project/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile"));
    }

    @Test
    public void markAsCompleteSpendProfileWhenSpendHigherThanEligible() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .build();

        SpendProfileTableResource table = buildSpendProfileTableResource(projectResource);

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        when(projectFinanceService.getSpendProfileTable(projectResource.getId(), organisationId)).thenReturn(table);

        when(projectFinanceService.markSpendProfile(projectId, organisationId, true)).thenReturn(serviceFailure(SPEND_PROFILE_CANNOT_MARK_AS_COMPLETE_BECAUSE_SPEND_HIGHER_THAN_ELIGIBLE));

        ProjectSpendProfileViewModel expectedViewModel = buildExpectedProjectSpendProfileViewModel(organisationId, projectResource, table);

        expectedViewModel.setObjectErrors(Collections.singletonList(new ObjectError(SPEND_PROFILE_CANNOT_MARK_AS_COMPLETE_BECAUSE_SPEND_HIGHER_THAN_ELIGIBLE.getErrorKey(), "Cannot mark as complete, because totals more than eligible")));

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/complete", projectId, organisationId)
        )
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("project/spend-profile"));
    }

    @Test
    public void markAsCompleteSpendProfileSuccess() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        when(projectFinanceService.markSpendProfile(projectId, organisationId, true)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/complete", projectId, organisationId)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/project/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile"));
    }

    @Test
    public void editSpendProfileSuccess() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .build();

        SpendProfileTableResource table = buildSpendProfileTableResource(projectResource);

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        when(projectFinanceService.getSpendProfileTable(projectResource.getId(), organisationId)).thenReturn(table);

        ProjectSpendProfileViewModel expectedViewModel = buildExpectedProjectSpendProfileViewModel(organisationId, projectResource, table);

        SpendProfileForm expectedForm = new SpendProfileForm();
        expectedForm.setTable(table);

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/edit", projectId, organisationId)
        )
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(view().name("project/spend-profile"));
    }

    private SpendProfileTableResource buildSpendProfileTableResource(ProjectResource projectResource) {

        SpendProfileTableResource expectedTable = new SpendProfileTableResource();

        expectedTable.setMarkedAsComplete(false);

        expectedTable.setMonths(asList(
                new LocalDateResource(2018, 3, 1),
                new LocalDateResource(2018, 4, 1),
                new LocalDateResource(2018, 5, 1)
        ));

        expectedTable.setEligibleCostPerCategoryMap(asMap(
                "Labour", new BigDecimal("100"),
                "Materials", new BigDecimal("150"),
                "Other costs", new BigDecimal("55")));

        expectedTable.setMonthlyCostsPerCategoryMap(asMap(
                "Labour", asList(new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("40")),
                "Materials", asList(new BigDecimal("70"), new BigDecimal("50"), new BigDecimal("60")),
                "Other costs", asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("0"))));


        List<LocalDate> months = IntStream.range(0, projectResource.getDurationInMonths().intValue()).mapToObj(projectResource.getTargetStartDate()::plusMonths).collect(toList());
        List<LocalDateResource> monthResources = simpleMap(months, LocalDateResource::new);

        expectedTable.setMonths(monthResources);

        return expectedTable;
    }

    private ProjectSpendProfileViewModel buildExpectedProjectSpendProfileViewModel(Long organisationId, ProjectResource projectResource, SpendProfileTableResource expectedTable) {
        List<SpendProfileSummaryYearModel> years = createSpendProfileSummaryYears();

        SpendProfileSummaryModel summary = new SpendProfileSummaryModel(years);

        // Build the expectedCategoryToActualTotal map based on the input
        Map<String, BigDecimal> expectedCategoryToActualTotal = new LinkedHashMap<>();
        expectedCategoryToActualTotal.put("Labour", new BigDecimal("100"));
        expectedCategoryToActualTotal.put("Materials", new BigDecimal("180"));
        expectedCategoryToActualTotal.put("Other costs", new BigDecimal("55"));

        // Expected total for each month based on the input
        List<BigDecimal> expectedTotalForEachMonth = asList(new BigDecimal("150"), new BigDecimal("85"), new BigDecimal("100"));

        // Assert that the total of totals is correct for Actual Costs and Eligible Costs based on the input
        BigDecimal expectedTotalOfAllActualTotals = new BigDecimal("335");
        BigDecimal expectedTotalOfAllEligibleTotals = new BigDecimal("305");

        // Assert that the view model is populated with the correct values
        return new ProjectSpendProfileViewModel(projectResource, organisationId, expectedTable,
                summary, false, expectedCategoryToActualTotal, expectedTotalForEachMonth,
                expectedTotalOfAllActualTotals, expectedTotalOfAllEligibleTotals);
    }

    private List<SpendProfileSummaryYearModel> createSpendProfileSummaryYears() {
        return asList(new SpendProfileSummaryYearModel(2017, "150"), new SpendProfileSummaryYearModel(2018, "185"));
    }
}
