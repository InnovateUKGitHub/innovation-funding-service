package org.innovateuk.ifs.project.financecheck.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.financechecks.controller.ProjectFinanceController;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.payload.PayloadDocumentation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.EligibilityDocs.eligibilityResourceFields;
import static org.innovateuk.ifs.documentation.ViabilityDocs.viabilityResourceFields;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostBuilder.newGrantClaimPercentage;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostCategoryBuilder.newGrantClaimCostCategory;
import static org.innovateuk.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static org.innovateuk.ifs.finance.builder.LabourCostCategoryBuilder.newLabourCostCategory;
import static org.innovateuk.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static org.innovateuk.ifs.finance.builder.OtherCostBuilder.newOtherCost;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostBuilder.newOtherFunding;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostCategoryBuilder.newOtherFundingCostCategory;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.finance.resource.OrganisationSize.SMALL;
import static org.innovateuk.ifs.finance.resource.category.LabourCostCategory.WORKING_DAYS_PER_YEAR;
import static org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory.OTHER_FUNDING;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceControllerDocumentation extends BaseControllerMockMVCTest<ProjectFinanceController> {

    @Mock
    private FinanceCheckService financeCheckServiceMock;

    @Mock
    private ProjectFinanceRowService projectFinanceRowServiceMock;

    @Override
    protected ProjectFinanceController supplyControllerUnderTest() {
        return new ProjectFinanceController();
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

        when(financeCheckServiceMock.getViability(projectOrganisationCompositeId)).thenReturn(serviceSuccess(expectedViabilityResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/viability", projectId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedViabilityResource)))
                .andDo(document("project/{method-name}",
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

        when(financeCheckServiceMock.saveViability(projectOrganisationCompositeId, viability, viabilityRagStatus)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/viability/{viability}/{viabilityRagStatus}", projectId, organisationId, viability, viabilityRagStatus)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
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

        EligibilityResource expectedEligibilityResource = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        expectedEligibilityResource.setEligibilityApprovalDate(LocalDate.now());
        expectedEligibilityResource.setEligibilityApprovalUserFirstName("Lee");
        expectedEligibilityResource.setEligibilityApprovalUserLastName("Bowman");

        when(financeCheckServiceMock.getEligibility(projectOrganisationCompositeId)).thenReturn(serviceSuccess(expectedEligibilityResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/eligibility", projectId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
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

        EligibilityState eligibility = EligibilityState.APPROVED;
        EligibilityRagStatus eligibilityRagStatus = EligibilityRagStatus.GREEN;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(financeCheckServiceMock.saveEligibility(projectOrganisationCompositeId, eligibility, eligibilityRagStatus)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/eligibility/{eligibility}/{eligibilityRagStatus}", projectId, organisationId, eligibility, eligibilityRagStatus)
                .header("IFS_AUTH_TOKEN", "123abc"))
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
        when(financeCheckServiceMock.getCreditReport(123L, 234L)).thenReturn(serviceSuccess(Boolean.TRUE));
        mockMvc.perform(get(url, 123L, 234L)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to which the credit report flag is linked"),
                                parameterWithName("organisationId").description("Id of the organisation to which the credit report flag is linked")
                        )
                ));
    }

    @Test
    public void setCreditReport() throws Exception {
        String url = "/project/{projectId}/partner-organisation/{organisationId}/credit-report/{reportPresent}";
        when(financeCheckServiceMock.saveCreditReport(123L, 234L, Boolean.TRUE)).thenReturn(serviceSuccess());
        mockMvc.perform(post(url, 123L, 234L, Boolean.TRUE)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
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
                withCompaniesHouseNumber("123456789").
                build();

        OrganisationResource academicOrganisation = newOrganisationResource().
                withName("Academic Org").
                withCompaniesHouseNumber("987654321").
                build();

        ProjectResource project = newProjectResource().build();

        Map<FinanceRowType, FinanceRowCostCategory> industrialOrganisationFinances = asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().withCosts(
                        newLabourCost().
                                withGrossEmployeeCost(new BigDecimal("10000.23"), new BigDecimal("5100.11"), BigDecimal.ZERO).
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
                        newGrantClaimPercentage().
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
                                withGrossEmployeeCost(new BigDecimal("10000.23"), new BigDecimal("5100.11"), new BigDecimal("600.11"), BigDecimal.ZERO).
                                withDescription("Developers", "Testers", "Something else", WORKING_DAYS_PER_YEAR).
                                withLabourDays(100, 120, 120, 250).
                                withName("direct_staff", "direct_staff", "exceptions_staff").
                                build(4)).
                        build(),
                FinanceRowType.OTHER_COSTS, newDefaultCostCategory().withCosts(
                        newOtherCost().
                                withCost(new BigDecimal("33.33"), new BigDecimal("98.51")).
                                withDescription("direct_costs", "exceptions_costs").
                                build(2)).
                        build(),
                FinanceRowType.FINANCE, newGrantClaimCostCategory().withCosts(
                        newGrantClaimPercentage().
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
                withOrganisationSize(SMALL).
                build(2);

        when(financeCheckServiceMock.getProjectFinances(projectId)).thenReturn(serviceSuccess(expectedFinances));

        mockMvc.perform(get("/project/{projectId}/project-finances", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedFinances)))
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which finance totals are being retrieved")
                        ),
                        PayloadDocumentation.relaxedResponseFields(ProjectFinanceResponseFields.projectFinanceFields)
                ));
    }
}
