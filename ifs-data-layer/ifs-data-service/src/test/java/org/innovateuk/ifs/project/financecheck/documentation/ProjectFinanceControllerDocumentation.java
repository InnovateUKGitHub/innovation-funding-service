package org.innovateuk.ifs.project.financecheck.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.financechecks.controller.ProjectFinanceController;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.payload.PayloadDocumentation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.ExcludedCostCategoryBuilder.newExcludedCostCategory;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostBuilder.newGrantClaimPercentage;
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
                FinanceRowType.FINANCE, newExcludedCostCategory().withCosts(
                        newGrantClaimPercentage().
                                withGrantClaimPercentage(BigDecimal.valueOf(30)).
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
                FinanceRowType.FINANCE, newExcludedCostCategory().withCosts(
                        newGrantClaimPercentage().
                                withGrantClaimPercentage(BigDecimal.valueOf(100)).
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
