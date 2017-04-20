package org.innovateuk.ifs.project.financecheck.documentation;

import au.com.bytecode.opencsv.CSVWriter;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.rest.LocalDateResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.builder.SpendProfileResourceBuilder;
import org.innovateuk.ifs.project.projectdetails.controller.ProjectFinanceController;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.PayloadDocumentation;

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
import static org.innovateuk.ifs.documentation.EligibilityDocs.eligibilityResourceFields;
import static org.innovateuk.ifs.documentation.SpendProfileDocs.*;
import static org.innovateuk.ifs.documentation.ViabilityDocs.viabilityResourceFields;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostBuilder.newGrantClaim;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostCategoryBuilder.newGrantClaimCostCategory;
import static org.innovateuk.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static org.innovateuk.ifs.finance.builder.LabourCostCategoryBuilder.newLabourCostCategory;
import static org.innovateuk.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static org.innovateuk.ifs.finance.builder.OtherCostBuilder.newOtherCost;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostBuilder.newOtherFunding;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostCategoryBuilder.newOtherFundingCostCategory;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.finance.resource.category.LabourCostCategory.WORKING_DAYS_PER_YEAR;
import static org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory.OTHER_FUNDING;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
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

        when(spendProfileServiceMock.generateSpendProfile(123L)).thenReturn(serviceSuccess());

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

        when(spendProfileServiceMock.approveOrRejectSpendProfile(isA(Long.class), isA(ApprovalType.class)))
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

        when(spendProfileServiceMock.getSpendProfileStatusByProjectId(isA(Long.class)))
                .thenReturn(serviceSuccess(ApprovalType.APPROVED));

        mockMvc.perform(get("/project/{projectId}/spend-profile/approval", 123L))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(ApprovalType.APPROVED)))
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

        when(spendProfileServiceMock.getSpendProfileTable(projectOrganisationCompositeId)).thenReturn(serviceSuccess(table));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile-table", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(table)))
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

        when(spendProfileServiceMock.getSpendProfileCSV(projectOrganisationCompositeId)).thenReturn(serviceSuccess(spendProfileCSVResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile-csv", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(spendProfileCSVResource)))
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
        when(spendProfileServiceMock.getSpendProfileCSV(projectOrganisationCompositeId)).
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
        when(spendProfileServiceMock.getSpendProfileCSV(projectOrganisationCompositeId)).
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

        when(spendProfileServiceMock.getSpendProfileTable(projectOrganisationCompositeId)).
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

        when(spendProfileServiceMock.getSpendProfile(projectOrganisationCompositeId)).thenReturn(serviceSuccess(spendProfileResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(spendProfileResource)))
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

        when(spendProfileServiceMock.getSpendProfile(projectOrganisationCompositeId)).
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

        when(spendProfileServiceMock.saveSpendProfile(eq(projectOrganisationCompositeId), isA(SpendProfileTableResource.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectId, organisationId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(table)))
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

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(spendProfileServiceMock.markSpendProfileComplete(projectOrganisationCompositeId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/complete", projectId, organisationId)
        )
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the Spend Profile data is being marked as complete"),
                                parameterWithName("organisationId").description("Organisation Id for which the Spend Profile data is being marked as complete")
                        )
                ));
    }

    @Test
    public void markSpendProfileIncomplete() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(spendProfileServiceMock.markSpendProfileIncomplete(projectOrganisationCompositeId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/incomplete", projectId, organisationId)
        )
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the Spend Profile data is being marked as incomplete"),
                                parameterWithName("organisationId").description("Organisation Id for which the Spend Profile data is being marked as incomplete")
                        )
                ));
    }

    @Test
    public void getViability() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        ViabilityResource expectedViabilityResource = new ViabilityResource(Viability.APPROVED, ViabilityRagStatus.GREEN);
        expectedViabilityResource.setViabilityApprovalDate(LocalDate.now());
        expectedViabilityResource.setViabilityApprovalUserFirstName("Lee");
        expectedViabilityResource.setViabilityApprovalUserLastName("Bowman");

        when(spendProfileServiceMock.getViability(projectOrganisationCompositeId)).thenReturn(serviceSuccess(expectedViabilityResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/viability", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedViabilityResource)))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which viability is being retrieved"),
                                parameterWithName("organisationId").description("Organisation Id for which viability is being retrieved")
                        ),
                        responseFields(viabilityResourceFields)
                ));
    }

    @Test
    public void saveViability() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;
        Viability viability = Viability.APPROVED;
        ViabilityRagStatus viabilityRagStatus = ViabilityRagStatus.GREEN;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(spendProfileServiceMock.saveViability(projectOrganisationCompositeId, viability, viabilityRagStatus)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/viability/{viability}/{viabilityRagStatus}", projectId, organisationId, viability, viabilityRagStatus)
        )
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which viability is being saved"),
                                parameterWithName("organisationId").description("Organisation Id for which viability is being saved"),
                                parameterWithName("viability").description("The viability being saved"),
                                parameterWithName("viabilityRagStatus").description("The viability RAG status being saved")
                        )
                ));
    }

    @Test
    public void getEligibility() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        EligibilityResource expectedEligibilityResource = new EligibilityResource(Eligibility.APPROVED, EligibilityRagStatus.GREEN);
        expectedEligibilityResource.setEligibilityApprovalDate(LocalDate.now());
        expectedEligibilityResource.setEligibilityApprovalUserFirstName("Lee");
        expectedEligibilityResource.setEligibilityApprovalUserLastName("Bowman");

        when(spendProfileServiceMock.getEligibility(projectOrganisationCompositeId)).thenReturn(serviceSuccess(expectedEligibilityResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/eligibility", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedEligibilityResource)))
                .andDo(document("project/partner-organisation/eligibility/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which eligibility is being retrieved"),
                                parameterWithName("organisationId").description("Organisation Id for which eligibility is being retrieved")
                        ),
                        responseFields(eligibilityResourceFields)
                        )
                );
    }

    @Test
    public void saveEligibility() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        Eligibility eligibility = Eligibility.APPROVED;
        EligibilityRagStatus eligibilityRagStatus = EligibilityRagStatus.GREEN;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(spendProfileServiceMock.saveEligibility(projectOrganisationCompositeId, eligibility, eligibilityRagStatus)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/eligibility/{eligibility}/{eligibilityRagStatus}", projectId, organisationId, eligibility, eligibilityRagStatus)
        )
                .andExpect(status().isOk())
                .andDo(document("project/partner-organisation/eligibility/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which eligibility is being saved"),
                                parameterWithName("organisationId").description("Organisation Id for which eligibility is being saved"),
                                parameterWithName("eligibility").description("The eligibility being saved"),
                                parameterWithName("eligibilityRagStatus").description("The eligibility RAG status being saved")
                        )
                ));
    }

    @Test
    public void getCreditReport() throws Exception {
        String url = "/project/{projectId}/partner-organisation/{organisationId}/credit-report";
        when(spendProfileServiceMock.getCreditReport(123L, 234L)).thenReturn(serviceSuccess(Boolean.TRUE));
        mockMvc.perform(get(url, 123L, 234L)).
                andExpect(status().isOk()).
                andExpect(content().string("true")).
                andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to which the credit report flag is linked"),
                                parameterWithName("organisationId").description("Id of the organisation to which the credit report flag is linked")
                        )
                ));
    }

    @Test
    public void setCreditReport() throws Exception {
        String url = "/project/{projectId}/partner-organisation/{organisationId}/credit-report/{reportPresent}";
        when(spendProfileServiceMock.saveCreditReport(123L, 234L, Boolean.TRUE)).thenReturn(serviceSuccess());
        mockMvc.perform(post(url, 123L, 234L, Boolean.TRUE)).
                andExpect(status().isOk()).
                andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to which the credit report flag is linked"),
                                parameterWithName("organisationId").description("Id of the organisation to which the credit report flag is linked"),
                                parameterWithName("reportPresent").description("The credit report flag")
                        )
                ));
    }

    @Test
    public void getProjectFinances() throws Exception {

        Long projectId = 1L;

        OrganisationResource industrialOrganisation = newOrganisationResource().
                withName("Industrial Org").
                withCompanyHouseNumber("123456789").
                build();

        OrganisationResource academicOrganisation = newOrganisationResource().
                withName("Academic Org").
                withCompanyHouseNumber("987654321").
                build();

        ProjectResource project = newProjectResource().build();

        Map<FinanceRowType, FinanceRowCostCategory> industrialOrganisationFinances = asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().withCosts(
                        newLabourCost().
                                withGrossAnnualSalary(new BigDecimal("10000.23"), new BigDecimal("5100.11"), BigDecimal.ZERO).
                                withDescription("Developers", "Testers", WORKING_DAYS_PER_YEAR).
                                withLabourDays(100, 120, 250).
                                build(3)).
                        build(),
                FinanceRowType.MATERIALS, newDefaultCostCategory().withCosts(
                        newMaterials().
                                withCost(new BigDecimal("33.33"), new BigDecimal("98.51")).
                                withQuantity(1, 2).
                                build(2)).
                        build(),
                FinanceRowType.FINANCE, newGrantClaimCostCategory().withCosts(
                        newGrantClaim().
                                withGrantClaimPercentage(30).
                                build(1)).
                        build(),
                FinanceRowType.OTHER_FUNDING, newOtherFundingCostCategory().withCosts(
                        newOtherFunding().
                                withOtherPublicFunding("Yes", "").
                                withFundingSource(OTHER_FUNDING, "Some source of funding").
                                withFundingAmount(null, BigDecimal.valueOf(1000)).
                                build(2)).
                        build());

        Map<FinanceRowType, FinanceRowCostCategory> academicOrganisationFinances = asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().withCosts(
                        newLabourCost().
                                withGrossAnnualSalary(new BigDecimal("10000.23"), new BigDecimal("5100.11"), new BigDecimal("600.11"), BigDecimal.ZERO).
                                withDescription("Developers", "Testers", "Something else", WORKING_DAYS_PER_YEAR).
                                withLabourDays(100, 120, 120, 250).
                                withName("direct_staff", "direct_staff", "exceptions_staff").
                                build(4)).
                        build(),
                FinanceRowType.OTHER_COSTS, newDefaultCostCategory().withCosts(
                        newOtherCost().
                                withCost(new BigDecimal("33.33"), new BigDecimal("98.51")).
                                withName("direct_costs", "exceptions_costs").
                                build(2)).
                        build(),
                FinanceRowType.FINANCE, newGrantClaimCostCategory().withCosts(
                        newGrantClaim().
                                withGrantClaimPercentage(100).
                                build(1)).
                        build(),
                FinanceRowType.OTHER_FUNDING, newOtherFundingCostCategory().withCosts(
                        newOtherFunding().
                                withOtherPublicFunding("Yes", "").
                                withFundingSource(OTHER_FUNDING, "Some source of funding").
                                withFundingAmount(null, BigDecimal.valueOf(1000)).
                                build(2)).
                        build());

        List<ProjectFinanceResource> expectedFinances = newProjectFinanceResource().
                withProject(project.getId()).
                withOrganisation(academicOrganisation.getId(), industrialOrganisation.getId()).
                withFinanceOrganisationDetails(academicOrganisationFinances, industrialOrganisationFinances).
                withOrganisationSize(1L).
                build(2);

        when(spendProfileServiceMock.getProjectFinances(projectId)).thenReturn(serviceSuccess(expectedFinances));

        mockMvc.perform(get("/project/{projectId}/project-finances", projectId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedFinances)))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which finance totals are being retrieved")
                        ),
                        PayloadDocumentation.responseFields(ProjectFinanceResponseFields.projectFinanceFields)
                ));
    }

    private Map<Long, BigDecimal> buildEligibleCostPerCategoryMap() {

        Map<Long, BigDecimal> eligibleCostPerCategoryMap = new LinkedHashMap<>();

        eligibleCostPerCategoryMap.put(1L, new BigDecimal("240"));
        eligibleCostPerCategoryMap.put(2L, new BigDecimal("190"));
        eligibleCostPerCategoryMap.put(3L, new BigDecimal("149"));

        return eligibleCostPerCategoryMap;
    }

    private Map<Long, List<BigDecimal>> buildSpendProfileCostsPerCategoryMap() {

        Map<Long, List<BigDecimal>> spendProfileCostsPerCategoryMap = new LinkedHashMap<>();

        spendProfileCostsPerCategoryMap.put(1L, asList(new BigDecimal("100"), new BigDecimal("120"), new BigDecimal("20")));
        spendProfileCostsPerCategoryMap.put(2L, asList(new BigDecimal("90"), new BigDecimal("50"), new BigDecimal("50")));
        spendProfileCostsPerCategoryMap.put(3L, asList(new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("149")));

        return spendProfileCostsPerCategoryMap;
    }

    @Override
    protected ProjectFinanceController supplyControllerUnderTest() {
        return new ProjectFinanceController();
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
