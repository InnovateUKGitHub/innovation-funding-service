package com.worth.ifs.project;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.error.exception.ObjectNotFoundException;
import com.worth.ifs.project.builder.SpendProfileResourceBuilder;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.project.viewmodel.ProjectSpendProfileViewModel;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
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
    public void viewSpendProfileWhenProjectDetailsNotInDB() throws Exception {

        Long organisationId = 1L;

        ProjectResource projectResource = newProjectResource().build();

        when(projectService.getById(projectResource.getId())).
                thenThrow(new ObjectNotFoundException("Project not found", null));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectResource.getId(), organisationId))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeDoesNotExist("model"));

        verify(projectService, never()).getSpendProfile(projectResource.getId(), organisationId);
    }

    @Test
    public void viewSpendProfileWhenSpendProfileDetailsNotInDB() throws Exception {

        Long organisationId = 1L;

        ProjectResource projectResource = newProjectResource().build();

        when(projectService.getById(projectResource.getId())).
                thenReturn(projectResource);

        when(projectService.getSpendProfile(projectResource.getId(), organisationId)).
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

        SpendProfileResource spendProfileResource = SpendProfileResourceBuilder.newSpendProfileResource()
                .withEligibleCostPerCategoryMap(buildEligibleCostPerCategoryMap())
                .build();

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        when(projectService.getSpendProfile(projectResource.getId(), organisationId)).thenReturn(spendProfileResource);

        SpendProfileTableResource expectedTable = buildExpectedSpendProfileTable();

        // Assert that the view model is populated with the correct values
        ProjectSpendProfileViewModel expectedViewModel = new ProjectSpendProfileViewModel(projectResource, expectedTable);

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectResource.getId(), organisationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("project/spend-profile"));

    }

    private Map<String, BigDecimal> buildEligibleCostPerCategoryMap() {

        Map<String, BigDecimal> eligibleCostPerCategoryMap = new LinkedHashMap<>();

        eligibleCostPerCategoryMap.put("LabourCost", new BigDecimal("240"));
        eligibleCostPerCategoryMap.put("CapitalCost", new BigDecimal("190"));
        eligibleCostPerCategoryMap.put("OtherCost", new BigDecimal("149"));

        return eligibleCostPerCategoryMap;
    }

    private SpendProfileTableResource buildExpectedSpendProfileTable() {

        SpendProfileTableResource expectedTable = new SpendProfileTableResource();

        expectedTable.setMonths(buildExpectedMonths());

        expectedTable.setMonthlyCostsPerCategoryMap(buildExpectedMonthlyCostsPerCategoryMap());

        return expectedTable;
    }


    private List<LocalDate> buildExpectedMonths() {

        List<LocalDate> expectedMonths = new ArrayList<>();

        expectedMonths.add(LocalDate.of(2018, 03, 01));
        expectedMonths.add(LocalDate.of(2018, 04, 01));
        expectedMonths.add(LocalDate.of(2018, 05, 01));

        return expectedMonths;
    }

    private Map<String, List<BigDecimal>> buildExpectedMonthlyCostsPerCategoryMap() {

        Map<String, List<BigDecimal>> expectedMonthlyCostsPerCategoryMap = new LinkedHashMap<>();

        expectedMonthlyCostsPerCategoryMap.put("LabourCost",
                buildExpectedCategorySplitList(new BigDecimal("80"), new BigDecimal("80"), new BigDecimal("80"), new BigDecimal("240")));
        expectedMonthlyCostsPerCategoryMap.put("CapitalCost",
                buildExpectedCategorySplitList(new BigDecimal("64"), new BigDecimal("63"), new BigDecimal("63"), new BigDecimal("190")));
        expectedMonthlyCostsPerCategoryMap.put("OtherCost",
                buildExpectedCategorySplitList(new BigDecimal("51"), new BigDecimal("49"), new BigDecimal("49"), new BigDecimal("149")));

        return expectedMonthlyCostsPerCategoryMap;
    }


    private List<BigDecimal> buildExpectedCategorySplitList(BigDecimal ...bigDecimals) {

        List<BigDecimal> expectedCategorySplit = new ArrayList<>();

        Stream.of(bigDecimals).forEach(bigDecimal -> expectedCategorySplit.add(bigDecimal));

        return expectedCategorySplit;

    }
}
