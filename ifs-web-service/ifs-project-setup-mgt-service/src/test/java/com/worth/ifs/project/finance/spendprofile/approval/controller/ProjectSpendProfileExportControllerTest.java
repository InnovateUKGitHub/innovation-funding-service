package com.worth.ifs.project.finance.spendprofile.approval.controller;

import au.com.bytecode.opencsv.CSVWriter;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.SpendProfileCSVResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectSpendProfileExportControllerTest extends BaseControllerMockMVCTest<ProjectSpendProfileExportController> {

    @Override
    protected ProjectSpendProfileExportController supplyControllerUnderTest() {
        return new ProjectSpendProfileExportController();
    }

    @Test
    public void exportProjectPartnerSpendProfileAsCSV() throws Exception {
        Long organisationId = 1L;
        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .build();

        SpendProfileTableResource expectedTable = buildSpendProfileTableResource(projectResource);
        SpendProfileCSVResource expectedSpendProfileCSVResource = buildSpendProfileCSVResource(expectedTable);
        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);
        when(projectFinanceService.getSpendProfileTable(projectResource.getId(), organisationId)).thenReturn(expectedTable);
        when(projectFinanceService.getSpendProfileCSV(projectResource.getId(), organisationId)).thenReturn(expectedSpendProfileCSVResource);

      mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile-export/csv", projectResource.getId(), organisationId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(("text/csv")))
                .andExpect(header().string("Content-Type", "text/csv"))
                .andExpect(header().string("Content-disposition", "attachment;filename=TEST_Spend_Profile_2016-10-30_10-11-12.csv"))
                .andExpect(content().string(generateTestCSVData(expectedTable)));
    }


    private SpendProfileCSVResource buildSpendProfileCSVResource(SpendProfileTableResource spendProfileTableResource) throws IOException {
        SpendProfileCSVResource spendProfileCSVResource = new SpendProfileCSVResource();
        spendProfileCSVResource.setCsvData(generateTestCSVData(spendProfileTableResource));
        spendProfileCSVResource.setFileName("TEST_Spend_Profile_2016-10-30_10-11-12.csv");
        return spendProfileCSVResource;
    }

    private String generateTestCSVData(SpendProfileTableResource spendProfileTableResource) throws IOException {
        // Build the expectedCategoryToActualTotal map based on the input
        Map<Long, BigDecimal> expectedCategoryToActualTotal = new LinkedHashMap<>();
        expectedCategoryToActualTotal.put(1L, new BigDecimal("100"));
        expectedCategoryToActualTotal.put(2L, new BigDecimal("180"));
        expectedCategoryToActualTotal.put(3L, new BigDecimal("55"));

        // Expected total for each month based on the input
        List<BigDecimal> expectedTotalForEachMonth = asList(new BigDecimal("150"), new BigDecimal("85"), new BigDecimal("100"));

        // Assert that the total of totals is correct for Actual Costs and Eligible Costs based on the input
        BigDecimal expectedTotalOfAllActualTotals = new BigDecimal("335");
        BigDecimal expectedTotalOfAllEligibleTotals = new BigDecimal("305");


        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);
        ArrayList<String[]> rows = new ArrayList<>();
        ArrayList<String> monthsRow = new ArrayList<>();
        monthsRow.add("Month");
        spendProfileTableResource.getMonths().forEach(
                value -> monthsRow.add(value.getLocalDate().toString()));
        monthsRow.add("TOTAL");
        monthsRow.add("Eligible Costs Total");
        rows.add(monthsRow.stream().toArray(String[]::new));

        ArrayList<String> byCategory = new ArrayList<>();
        spendProfileTableResource.getMonthlyCostsPerCategoryMap().forEach((category, values)-> {
            byCategory.add(String.valueOf(category));
            values.forEach(val -> {
                byCategory.add(val.toString());
            });
            byCategory.add(expectedCategoryToActualTotal.get(category).toString());
            byCategory.add(spendProfileTableResource.getEligibleCostPerCategoryMap().get(category).toString());
            rows.add(byCategory.stream().toArray(String[]::new));
            byCategory.clear();
        });

        ArrayList<String> totals = new ArrayList<>();
        totals.add("TOTAL");
        expectedTotalForEachMonth.forEach(value -> totals.add(value.toString()));
        totals.add(expectedTotalOfAllActualTotals.toString());
        totals.add(expectedTotalOfAllEligibleTotals.toString());
        rows.add(totals.stream().toArray(String[]::new));
        csvWriter.writeAll(rows);
        csvWriter.close();
        return stringWriter.toString();
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
                1L, new BigDecimal("100"),
                2L, new BigDecimal("150"),
                3L, new BigDecimal("55")));

        expectedTable.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("40")),
                2L, asList(new BigDecimal("70"), new BigDecimal("50"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("0"))));


        List<LocalDate> months = IntStream.range(0, projectResource.getDurationInMonths().intValue()).mapToObj(projectResource.getTargetStartDate()::plusMonths).collect(toList());
        List<LocalDateResource> monthResources = simpleMap(months, LocalDateResource::new);

        expectedTable.setMonths(monthResources);

        return expectedTable;
    }
}
