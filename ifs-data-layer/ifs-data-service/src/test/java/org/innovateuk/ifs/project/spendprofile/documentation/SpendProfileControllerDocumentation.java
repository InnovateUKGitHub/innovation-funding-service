package org.innovateuk.ifs.project.spendprofile.documentation;

import com.opencsv.CSVWriter;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.rest.LocalDateResource;
import org.innovateuk.ifs.project.builder.SpendProfileResourceBuilder;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.spendprofile.controller.SpendProfileController;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileCSVResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileService;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.lang.Boolean.FALSE;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.SPEND_PROFILE_CSV_GENERATION_FAILURE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SpendProfileControllerDocumentation extends BaseControllerMockMVCTest<SpendProfileController> {


    @Mock
    private SpendProfileService spendProfileServiceMock;

    @Override
    protected SpendProfileController supplyControllerUnderTest() {
        return new SpendProfileController();
    }

    @Test
    public void generateSpendProfile() throws Exception {

        when(spendProfileServiceMock.generateSpendProfile(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/spend-profile/generate", 123L)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isCreated());
    }

    @Test
    public void approveOrRejectSpendProfile() throws Exception {

        when(spendProfileServiceMock.approveOrRejectSpendProfile(isA(Long.class), isA(ApprovalType.class)))
                .thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/spend-profile/approval/{approvalType}", 123L, ApprovalType.APPROVED)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void getSpendProfileStatusByProjectId() throws Exception {

        when(spendProfileServiceMock.getSpendProfileStatusByProjectId(isA(Long.class)))
                .thenReturn(serviceSuccess(ApprovalType.APPROVED));

        mockMvc.perform(get("/project/{projectId}/spend-profile/approval", 123L)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(ApprovalType.APPROVED)));
    }

    @Test
    public void getSpendProfileTable() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        SpendProfileTableResource table = new SpendProfileTableResource();
        table.setMarkedAsComplete(FALSE);
        table.setMonths(asList(new LocalDateResource(2016, 1, 1), new LocalDateResource(2016, 2, 1), new LocalDateResource(2016, 3, 1)));
        table.setEligibleCostPerCategoryMap(buildEligibleCostPerCategoryMap());
        table.setMonthlyCostsPerCategoryMap(buildSpendProfileCostsPerCategoryMap());

        when(spendProfileServiceMock.getSpendProfileTable(projectOrganisationCompositeId)).thenReturn(serviceSuccess(table));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile-table", projectId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(table)));
    }

    @Test
    public void getSpendProfileCsv() throws Exception {
        Long projectId = 1L;
        Long organisationId = 1L;
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .build();

        SpendProfileCSVResource spendProfileCSVResource = new SpendProfileCSVResource();
        spendProfileCSVResource.setCsvData(generateTestCSVData(buildSpendProfileTableResource(projectResource)));
        spendProfileCSVResource.setFileName("TEST_Spend_Profile_2016-10-03_10-11-12.csv");

        when(spendProfileServiceMock.getSpendProfileCSV(projectOrganisationCompositeId)).thenReturn(serviceSuccess(spendProfileCSVResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile-csv", projectId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(spendProfileCSVResource)));
    }

    @Test
    public void getSpendProfileCsvWhenSpendProfileDataNotInDb() throws Exception {
        Long projectId = 1L;
        Long organisationId = 1L;
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        when(spendProfileServiceMock.getSpendProfileCSV(projectOrganisationCompositeId)).
                thenReturn(serviceFailure(notFoundError(SpendProfileResource.class, projectId, organisationId)));
        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile-csv", projectId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getSpendProfileCsvWhenIoException() throws Exception {
        Long projectId = 1L;
        Long organisationId = 1L;
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        when(spendProfileServiceMock.getSpendProfileCSV(projectOrganisationCompositeId)).
                thenReturn(serviceFailure(SPEND_PROFILE_CSV_GENERATION_FAILURE));
        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile-csv", projectId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void getSpendProfileTableWhenSpendProfileDataNotInDb() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(spendProfileServiceMock.getSpendProfileTable(projectOrganisationCompositeId)).
                thenReturn(serviceFailure(notFoundError(SpendProfileResource.class, projectId, organisationId)));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile-table", projectId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getSpendProfile() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        SpendProfileResource spendProfileResource = SpendProfileResourceBuilder.newSpendProfileResource()
                .withOrganisation(1L)
                .withProject(2L)
                .withCostCategoryType(3L)
                .build();

        when(spendProfileServiceMock.getSpendProfile(projectOrganisationCompositeId)).thenReturn(serviceSuccess(spendProfileResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(spendProfileResource)));
    }

    @Test
    public void getSpendProfileWhenSpendProfileDataNotInDb() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(spendProfileServiceMock.getSpendProfile(projectOrganisationCompositeId)).
                thenReturn(serviceFailure(CommonErrors.notFoundError(SpendProfile.class, projectId, organisationId)));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void saveSpendProfile() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        SpendProfileTableResource table = new SpendProfileTableResource();
        table.setMarkedAsComplete(false);
        table.setMonths(asList(new LocalDateResource(2016, 1, 1), new LocalDateResource(2016, 2, 1), new LocalDateResource(2016, 3, 1)));
        table.setEligibleCostPerCategoryMap(buildEligibleCostPerCategoryMap());
        table.setMonthlyCostsPerCategoryMap(buildSpendProfileCostsPerCategoryMap());

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(spendProfileServiceMock.saveSpendProfile(eq(projectOrganisationCompositeId), isA(SpendProfileTableResource.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectId, organisationId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(table))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }


    @Test
    public void markSpendProfileComplete() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(spendProfileServiceMock.markSpendProfileComplete(projectOrganisationCompositeId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/complete", projectId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void markSpendProfileIncomplete() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(spendProfileServiceMock.markSpendProfileIncomplete(projectOrganisationCompositeId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/incomplete", projectId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void completeSpendProfilesReview() throws Exception {

        Long projectId = 1L;

        when(spendProfileServiceMock.completeSpendProfilesReview(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/complete-spend-profiles-review", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }


    private Map<Long, List<BigDecimal>> buildSpendProfileCostsPerCategoryMap() {

        Map<Long, List<BigDecimal>> spendProfileCostsPerCategoryMap = new LinkedHashMap<>();

        spendProfileCostsPerCategoryMap.put(1L, asList(new BigDecimal("100"), new BigDecimal("120"), new BigDecimal("20")));
        spendProfileCostsPerCategoryMap.put(2L, asList(new BigDecimal("90"), new BigDecimal("50"), new BigDecimal("50")));
        spendProfileCostsPerCategoryMap.put(3L, asList(new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("149")));

        return spendProfileCostsPerCategoryMap;
    }

    private String generateTestCSVData(SpendProfileTableResource spendProfileTableResource) throws IOException {
        Map<Long, BigDecimal> expectedCategoryToActualTotal = new LinkedHashMap<>();
        expectedCategoryToActualTotal.put(1L, new BigDecimal("100"));
        expectedCategoryToActualTotal.put(2L, new BigDecimal("180"));
        expectedCategoryToActualTotal.put(3L, new BigDecimal("55"));

        List<BigDecimal> expectedTotalForEachMonth = asList(new BigDecimal("150"), new BigDecimal("85"), new BigDecimal("100"));

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

    private Map<Long, BigDecimal> buildEligibleCostPerCategoryMap() {

        Map<Long, BigDecimal> eligibleCostPerCategoryMap = new LinkedHashMap<>();

        eligibleCostPerCategoryMap.put(1L, new BigDecimal("240"));
        eligibleCostPerCategoryMap.put(2L, new BigDecimal("190"));
        eligibleCostPerCategoryMap.put(3L, new BigDecimal("149"));

        return eligibleCostPerCategoryMap;
    }
}
