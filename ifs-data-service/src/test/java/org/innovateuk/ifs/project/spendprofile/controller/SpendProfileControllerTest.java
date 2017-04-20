package org.innovateuk.ifs.project.spendprofile.controller;

import au.com.bytecode.opencsv.CSVWriter;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.LocalDateResource;
import org.innovateuk.ifs.project.builder.SpendProfileResourceBuilder;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileCSVResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SpendProfileControllerTest extends BaseControllerMockMVCTest<SpendProfileController> {

    @Test
    public void testGenerateSpendProfile() throws Exception {
        when(spendProfileServiceMock.generateSpendProfile(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/123/spend-profile/generate")).andExpect(status().isCreated());

        verify(spendProfileServiceMock).generateSpendProfile(123L);
    }

    @Test
    public void testGenerateSpendProfileForPartnerOrganisation() throws Exception {
        when(spendProfileServiceMock.generateSpendProfileForPartnerOrganisation(1L, 2L, 7L))
                .thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/1/partner-organisation/2/user/7/spend-profile/generate"))
                .andExpect(status().isCreated());

        verify(spendProfileServiceMock).generateSpendProfileForPartnerOrganisation(1L, 2L, 7L);
    }

    @Test
    public void markSpendProfileComplete() throws Exception {
        final Long projectId = 1L;
        final Long organisationId = 1L;
        final SpendProfileTableResource table = new SpendProfileTableResource();
        final ProjectOrganisationCompositeId projectOrganisationCompositeId
                = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(spendProfileServiceMock.markSpendProfileComplete(projectOrganisationCompositeId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/complete",
                projectId, organisationId, true).contentType(APPLICATION_JSON).content(toJson(table)))
                .andExpect(status().isOk());

        verify(spendProfileServiceMock).markSpendProfileComplete(projectOrganisationCompositeId);
    }

    @Test
    public void markSpendProfileIncomplete() throws Exception {
        final Long projectId = 1L;
        final Long organisationId = 2L;
        final SpendProfileTableResource table = new SpendProfileTableResource();
        final ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(spendProfileServiceMock.markSpendProfileIncomplete(projectOrganisationCompositeId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/incomplete", projectId, organisationId, true)
                .contentType(APPLICATION_JSON)
                .content(toJson(table)))
                .andExpect(status().isOk());

        verify(spendProfileServiceMock).markSpendProfileIncomplete(projectOrganisationCompositeId);
    }


    @Test
    public void getSpendProfileTable() throws Exception {
        final Long projectId = 1L;
        final Long organisationId = 1L;
        final ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        final SpendProfileTableResource expectedTable = new SpendProfileTableResource();

        expectedTable.setMonths(asList(
                new LocalDateResource(2016, 2, 1),
                new LocalDateResource(2016, 3, 1),
                new LocalDateResource(2016, 4, 1)
        ));

        expectedTable.setEligibleCostPerCategoryMap(asMap(
                "Labour", new BigDecimal("100"),
                "Materials", new BigDecimal("150"),
                "Other costs", new BigDecimal("55")));

        expectedTable.setMonthlyCostsPerCategoryMap(asMap(
                "Labour", asList(new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("40")),
                "Materials", asList(new BigDecimal("70"), new BigDecimal("50"), new BigDecimal("60")),
                "Other costs", asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("0"))));

        when(spendProfileServiceMock.getSpendProfileTable(projectOrganisationCompositeId)).thenReturn(serviceSuccess(expectedTable));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile-table", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedTable)));
    }


    @Test
    public void getSpendProfileCsv() throws Exception {
        final Long projectId = 1L;
        final Long organisationId = 1L;
        final ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .withId(projectId)
                .build();
        final ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        final SpendProfileTableResource expectedTable = buildSpendProfileTableResource(projectResource);
        final SpendProfileCSVResource expectedResource = new SpendProfileCSVResource();

        expectedResource.setFileName("TEST_Spend_Profile_2016-10-30_10-11_12.csv");
        expectedResource.setCsvData(generateTestCSVDataUsing(expectedTable));

        when(spendProfileServiceMock.getSpendProfileCSV(projectOrganisationCompositeId)).thenReturn(serviceSuccess(expectedResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile-csv", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedResource)));
    }

    @Test
    public void getSpendProfile() throws Exception {
        final Long projectId = 1L;
        final Long organisationId = 1L;
        final ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        final SpendProfileResource spendProfileResource = SpendProfileResourceBuilder.newSpendProfileResource().build();

        when(spendProfileServiceMock.getSpendProfile(projectOrganisationCompositeId)).thenReturn(serviceSuccess(spendProfileResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(spendProfileResource)));
    }

    @Test
    public void saveSpendProfile() throws Exception {
        final Long projectId = 1L;
        final Long organisationId = 1L;
        final SpendProfileTableResource table = new SpendProfileTableResource();
        final ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(spendProfileServiceMock.saveSpendProfile(projectOrganisationCompositeId, table)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectId, organisationId)
                .contentType(APPLICATION_JSON)
                .content(toJson(table)))
                .andExpect(status().isOk());
    }


    @Test
    public void testCompleteSpendProfilesReview() throws Exception {
        final Long projectId = 1L;

        when(spendProfileServiceMock.completeSpendProfilesReview(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/complete-spend-profiles-review", projectId))
                .andExpect(status().isOk());
    }


    private String generateTestCSVDataUsing(SpendProfileTableResource spendProfileTableResource) throws IOException {
        // Build the expectedCategoryToActualTotal map based on the input
        final Map<Long, BigDecimal> expectedCategoryToActualTotal = new LinkedHashMap<>();
        expectedCategoryToActualTotal.put(1L, new BigDecimal("100"));
        expectedCategoryToActualTotal.put(2L, new BigDecimal("180"));
        expectedCategoryToActualTotal.put(3L, new BigDecimal("55"));

        // Expected total for each month based on the input
        final List<BigDecimal> expectedTotalForEachMonth = asList(new BigDecimal("150"), new BigDecimal("85"), new BigDecimal("100"));

        // Assert that the total of totals is correct for Actual Costs and Eligible Costs based on the input
        final BigDecimal expectedTotalOfAllActualTotals = new BigDecimal("335");
        final BigDecimal expectedTotalOfAllEligibleTotals = new BigDecimal("305");


        final StringWriter stringWriter = new StringWriter();
        final CSVWriter csvWriter = new CSVWriter(stringWriter);
        final ArrayList<String[]> rows = new ArrayList<>();
        final ArrayList<String> monthsRow = new ArrayList<>();
        monthsRow.add("Month");
        spendProfileTableResource.getMonths().forEach(value -> monthsRow.add(value.getLocalDate().toString()));
        monthsRow.add("TOTAL");
        monthsRow.add("Eligible Costs Total");
        rows.add(monthsRow.stream().toArray(String[]::new));

        ArrayList<String> byCategory = new ArrayList<>();
        spendProfileTableResource.getMonthlyCostsPerCategoryMap().forEach((category, values) -> {
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
        final SpendProfileTableResource expectedTable = new SpendProfileTableResource();
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
                2L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("0"))));


        final List<LocalDate> months = IntStream.range(0, projectResource.getDurationInMonths().intValue())
                .mapToObj(projectResource.getTargetStartDate()::plusMonths).collect(toList());
        final List<LocalDateResource> monthResources = simpleMap(months, LocalDateResource::new);

        expectedTable.setMonths(monthResources);

        return expectedTable;
    }


    @Override
    protected SpendProfileController supplyControllerUnderTest() {
        return new SpendProfileController();
    }
}
