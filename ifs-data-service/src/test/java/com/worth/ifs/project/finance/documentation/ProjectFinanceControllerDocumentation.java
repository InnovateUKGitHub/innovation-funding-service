package com.worth.ifs.project.finance.documentation;

import au.com.bytecode.opencsv.CSVWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.error.CommonErrors;
import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.project.builder.SpendProfileResourceBuilder;
import com.worth.ifs.project.controller.ProjectFinanceController;
import com.worth.ifs.project.finance.domain.SpendProfile;
import com.worth.ifs.project.resource.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.SPEND_PROFILE_CSV_GENERATION_FAILURE;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.SpendProfileDocs.spendProfileCSVFields;
import static com.worth.ifs.documentation.SpendProfileDocs.spendProfileResourceFields;
import static com.worth.ifs.documentation.SpendProfileDocs.spendProfileTableFields;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.lang.Boolean.FALSE;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceControllerDocumentation extends BaseControllerMockMVCTest<ProjectFinanceController> {

    private RestDocumentationResultHandler document;

    @Before
    public void setup(){
        this.document = document("project/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void generateSpendProfile() throws Exception {

        when(projectFinanceServiceMock.generateSpendProfile(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/spend-profile/generate", 123L)).
                andExpect(status().isCreated()).
                andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the " +
                                        "Spend Profile information is being generated")
                        )
                ));
    }

    @Test
    public void approveOrRejectSpendProfile() throws Exception {

        when(projectFinanceServiceMock.approveOrRejectSpendProfile(isA(Long.class), isA(ApprovalType.class)))
                .thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/spend-profile/approval/{approvalType}", 123L, ApprovalType.APPROVED))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the " +
                                        "Spend Profile information is being approved or rejected"),
                                parameterWithName("approvalType").description("New approval or rejection of the " +
                                        "Spend profile in this project")
                        )
                ));
    }

    @Test
    public void getSpendProfileStatusByProjectId() throws Exception {

        when(projectFinanceServiceMock.getSpendProfileStatusByProjectId(isA(Long.class)))
                .thenReturn(serviceSuccess(ApprovalType.APPROVED));

        mockMvc.perform(get("/project/{projectId}/spend-profile/approval", 123L))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(ApprovalType.APPROVED)))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the " +
                                        "Spend Profile status is requested")
                        )
                ));
    }

    @Test
    public void getSpendProfileTable()  throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        SpendProfileTableResource table = new SpendProfileTableResource();
        table.setMarkedAsComplete(FALSE);
        table.setMonths(asList(new LocalDateResource(2016, 1, 1), new LocalDateResource(2016, 2, 1), new LocalDateResource(2016, 3, 1)));
        table.setEligibleCostPerCategoryMap(buildEligibleCostPerCategoryMap());
        table.setMonthlyCostsPerCategoryMap(buildSpendProfileCostsPerCategoryMap());

        when(projectFinanceServiceMock.getSpendProfileTable(projectOrganisationCompositeId)).thenReturn(serviceSuccess(table));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile-table", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(table)))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the Spend Profile data is being retrieved"),
                                parameterWithName("organisationId").description("Organisation Id for which the Spend Profile data is being retrieved")
                        ),
                        responseFields(spendProfileTableFields)
                ));
    }

    @Test
    public void getSpendProfileCsv()  throws Exception {
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

        when(projectFinanceServiceMock.getSpendProfileCSV(projectOrganisationCompositeId)).thenReturn(serviceSuccess(spendProfileCSVResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile-csv", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(spendProfileCSVResource)))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the Spend Profile data is being retrieved"),
                                parameterWithName("organisationId").description("Organisation Id for which the Spend Profile data is being retrieved")
                        ),
                        responseFields(spendProfileCSVFields)
                ));
    }

    @Test
    public void getSpendProfileCsvWhenSpendProfileDataNotInDb()  throws Exception {
        Long projectId = 1L;
        Long organisationId = 1L;
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        when(projectFinanceServiceMock.getSpendProfileCSV(projectOrganisationCompositeId)).
                thenReturn(serviceFailure(notFoundError(SpendProfileResource.class, projectId, organisationId)));
        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile-csv", projectId, organisationId))
                .andExpect(status().isNotFound())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the Spend Profile data is being retrieved"),
                                parameterWithName("organisationId").description("Organisation Id for which the Spend Profile data is being retrieved")
                        )
                ));
    }

    @Test
    public void getSpendProfileCsvWhenIoException()  throws Exception {
        Long projectId = 1L;
        Long organisationId = 1L;
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        when(projectFinanceServiceMock.getSpendProfileCSV(projectOrganisationCompositeId)).
                thenReturn(serviceFailure(SPEND_PROFILE_CSV_GENERATION_FAILURE));
        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile-csv", projectId, organisationId))
                .andExpect(status().is5xxServerError())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the Spend Profile data is being retrieved"),
                                parameterWithName("organisationId").description("Organisation Id for which the Spend Profile data is being retrieved")
                        )
                ));
    }

    @Test
    public void getSpendProfileTableWhenSpendProfileDataNotInDb()  throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(projectFinanceServiceMock.getSpendProfileTable(projectOrganisationCompositeId)).
                    thenReturn(serviceFailure(notFoundError(SpendProfileResource.class, projectId, organisationId)));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile-table", projectId, organisationId))
                .andExpect(status().isNotFound())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the Spend Profile data is being retrieved"),
                                parameterWithName("organisationId").description("Organisation Id for which the Spend Profile data is being retrieved")
                        )
                ));
    }

    @Test
    public void getSpendProfile()  throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        SpendProfileResource spendProfileResource = SpendProfileResourceBuilder.newSpendProfileResource()
                .withOrganisation(1L)
                .withProject(2L)
                .withCostCategoryType(3L)
                .build();

        when(projectFinanceServiceMock.getSpendProfile(projectOrganisationCompositeId)).thenReturn(serviceSuccess(spendProfileResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(spendProfileResource)))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the Spend Profile data is being retrieved"),
                                parameterWithName("organisationId").description("Organisation Id for which the Spend Profile data is being retrieved")
                        ),
                        responseFields(spendProfileResourceFields)
                ));
    }

    @Test
    public void getSpendProfileWhenSpendProfileDataNotInDb()  throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(projectFinanceServiceMock.getSpendProfile(projectOrganisationCompositeId)).
                    thenReturn(serviceFailure(CommonErrors.notFoundError(SpendProfile.class, projectId, organisationId)));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectId, organisationId)
        )
                .andExpect(status().isNotFound())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the Spend Profile data is being retrieved"),
                                parameterWithName("organisationId").description("Organisation Id for which the Spend Profile data is being retrieved")
                        )
                ));
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

        when(projectFinanceServiceMock.saveSpendProfile(eq(projectOrganisationCompositeId), isA(SpendProfileTableResource.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectId, organisationId)
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(table)))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the Spend Profile data is being saved"),
                                parameterWithName("organisationId").description("Organisation Id for which the Spend Profile data is being saved")
                        ),
                        requestFields(spendProfileTableFields)
                ));
    }


    @Test
    public void markSpendProfileComplete() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;
        Boolean complete = true;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(projectFinanceServiceMock.markSpendProfile(projectOrganisationCompositeId, complete)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/complete/{complete}", projectId, organisationId, complete)
        )
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the Spend Profile data is being marked as complete"),
                                parameterWithName("organisationId").description("Organisation Id for which the Spend Profile data is being marked as complete"),
                                parameterWithName("complete").description("Flag to indicate if the Spend Profile can be marked as complete or not")
                        )
                ));
    }

    private Map<String, BigDecimal> buildEligibleCostPerCategoryMap() {

        Map<String, BigDecimal> eligibleCostPerCategoryMap = new LinkedHashMap<>();

        eligibleCostPerCategoryMap.put("LabourCost", new BigDecimal("240"));
        eligibleCostPerCategoryMap.put("CapitalCost", new BigDecimal("190"));
        eligibleCostPerCategoryMap.put("OtherCost", new BigDecimal("149"));

        return eligibleCostPerCategoryMap;
    }

    private Map<String, List<BigDecimal>> buildSpendProfileCostsPerCategoryMap() {

        Map<String, List<BigDecimal>> spendProfileCostsPerCategoryMap = new LinkedHashMap<>();

        spendProfileCostsPerCategoryMap.put("LabourCost", asList(new BigDecimal("100"), new BigDecimal("120"), new BigDecimal("20")));
        spendProfileCostsPerCategoryMap.put("CapitalCost", asList(new BigDecimal("90"), new BigDecimal("50"), new BigDecimal("50")));
        spendProfileCostsPerCategoryMap.put("OtherCost", asList(new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("149")));

        return spendProfileCostsPerCategoryMap;
    }

    @Override
    protected ProjectFinanceController supplyControllerUnderTest() {
        return new ProjectFinanceController();
    }

    private String generateTestCSVData(SpendProfileTableResource spendProfileTableResource) throws IOException {
        Map<String, BigDecimal> expectedCategoryToActualTotal = new LinkedHashMap<>();
        expectedCategoryToActualTotal.put("Labour", new BigDecimal("100"));
        expectedCategoryToActualTotal.put("Materials", new BigDecimal("180"));
        expectedCategoryToActualTotal.put("Other costs", new BigDecimal("55"));

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
        spendProfileTableResource.getMonthlyCostsPerCategoryMap().forEach((category, values)-> {
            byCategory.add(category);
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
}
