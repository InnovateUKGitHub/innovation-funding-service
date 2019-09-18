package org.innovateuk.ifs.project.spendprofile.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryGroup;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryType;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.AcademicCostBuilder.newAcademicCost;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static org.innovateuk.ifs.finance.builder.LabourCostCategoryBuilder.newLabourCostCategory;
import static org.innovateuk.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.finance.resource.category.LabourCostCategory.WORKING_DAYS_PER_YEAR;
import static org.innovateuk.ifs.finance.resource.cost.AcademicCostCategoryGenerator.*;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryGroupBuilder.newCostCategoryGroup;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryTypeBuilder.newCostCategoryType;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ByProjectFinanceCostCategorySummaryStrategyTest extends BaseServiceUnitTest<ByProjectFinanceCostCategorySummaryStrategy> {

    @Mock
    private CostCategoryTypeStrategy costCategoryTypeStrategyMock;

    @Mock
    private ProjectService projectServiceMock;

    @Mock
    private OrganisationService organisationServiceMock;

    @Mock
    private ProjectFinanceRowService projectFinanceRowServiceMock;

    @Mock
    private CompetitionService competitionServiceMock;

    @Test
    public void testGenerateSpendProfileForIndustrialOrganisation() {

        ProjectResource project = newProjectResource().
                withDuration(10L).
                withCompetition(2L).
                build();

        CompetitionResource competition = newCompetitionResource()
                .withIncludeJesForm(true)
                .build();

        OrganisationResource organisation = newOrganisationResource().withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build();

        Map<FinanceRowType, FinanceRowCostCategory> finances = asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().
                        withCosts(
                            newLabourCost().
                                    withGrossEmployeeCost(new BigDecimal("10000"), new BigDecimal("5100"), BigDecimal.ZERO).
                                    withDescription("Developers", "Testers", WORKING_DAYS_PER_YEAR).
                                    withLabourDays(100, 120, 250).
                                    build(3)).
                        build(),
                FinanceRowType.MATERIALS, newDefaultCostCategory().withCosts(
                        newMaterials().
                                withCost(new BigDecimal("33"), new BigDecimal("98")).
                                withQuantity(1, 2).
                                build(2)).
                        build());

        finances.forEach((type, category) -> category.calculateTotal());

        ProjectFinanceResource projectFinance = newProjectFinanceResource().
                withFinanceOrganisationDetails(finances).
                build();

        List<CostCategory> costCategories = newCostCategory().
                withName("Labour", "Materials").
                build(2);

        CostCategoryGroup costCategoryGroup = newCostCategoryGroup().
                withCostCategories(costCategories).
                build();

        CostCategoryType costCategoryType = newCostCategoryType().withCostCategoryGroup(costCategoryGroup).build();

        when(projectServiceMock.getProjectById(project.getId())).thenReturn(serviceSuccess(project));
        when(organisationServiceMock.findById(organisation.getId())).thenReturn(serviceSuccess(organisation));
        when(projectFinanceRowServiceMock.financeChecksDetails(project.getId(), organisation.getId())).thenReturn(serviceSuccess(projectFinance));
        when(competitionServiceMock.getCompetitionById(project.getCompetition())).thenReturn(serviceSuccess(competition));
        when(costCategoryTypeStrategyMock.getOrCreateCostCategoryTypeForSpendProfile(project.getId(), organisation.getId())).thenReturn(serviceSuccess(costCategoryType));

        ServiceResult<SpendProfileCostCategorySummaries> result = service.getCostCategorySummaries(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());

        SpendProfileCostCategorySummaries summaries = result.getSuccess();
        assertEquals(costCategoryType, summaries.getCostCategoryType());
        assertEquals(2, summaries.getCosts().size());

        SpendProfileCostCategorySummary summary1 = simpleFindFirst(summaries.getCosts(),
                s -> s.getCategory().equals(costCategories.get(0))).get();

        SpendProfileCostCategorySummary summary2 = simpleFindFirst(summaries.getCosts(),
                s -> s.getCategory().equals(costCategories.get(1))).get();


        assertEquals(new BigDecimal("6448"), summary1.getTotal());
        assertEquals(new BigDecimal("652"), summary1.getFirstMonthSpend());
        assertEquals(new BigDecimal("644"), summary1.getOtherMonthsSpend());

        assertEquals(new BigDecimal("229"), summary2.getTotal());
        assertEquals(new BigDecimal("31"), summary2.getFirstMonthSpend());
        assertEquals(new BigDecimal("22"), summary2.getOtherMonthsSpend());
    }

    @Test
    public void testGenerateSpendProfileForAcademicOrganisation() {
        ProjectResource project = newProjectResource().
                withDuration(10L).
                withCompetition(2L).
                build();

        CompetitionResource competition = newCompetitionResource()
                .withIncludeJesForm(true)
                .build();

        OrganisationResource organisation = newOrganisationResource().withOrganisationType(OrganisationTypeEnum.RESEARCH.getId()).build();

        Map<FinanceRowType, FinanceRowCostCategory> finances = asMap(
                FinanceRowType.LABOUR, newDefaultCostCategory().withCosts(
                        newAcademicCost().
                                withCost(new BigDecimal("6448"), new BigDecimal("288")).
                                withName(DIRECTLY_INCURRED_STAFF.getFinanceRowName(), INDIRECT_COSTS_STAFF.getFinanceRowName()).
                                build(2)).
                        build(),
                FinanceRowType.OTHER_COSTS, newDefaultCostCategory().withCosts(
                        newAcademicCost().
                                withCost(new BigDecimal("33"), new BigDecimal("98")).
                                withName(DIRECTLY_INCURRED_OTHER_COSTS.getFinanceRowName(), INDIRECT_COSTS_OTHER_COSTS.getFinanceRowName()).
                                build(2)).
                        build());

        finances.forEach((type, category) -> category.calculateTotal());

        ProjectFinanceResource projectFinance = newProjectFinanceResource().
                withFinanceOrganisationDetails(finances).
                build();

        List<CostCategory> costCategories = newCostCategory().
                withName(DIRECTLY_INCURRED_STAFF.getName(),
                        INDIRECT_COSTS_STAFF.getName(),
                        DIRECTLY_INCURRED_OTHER_COSTS.getName(),
                        INDIRECT_COSTS_OTHER_COSTS.getName()).
                withLabel(DIRECTLY_INCURRED_STAFF.getLabel(),
                        INDIRECT_COSTS_STAFF.getLabel(),
                        DIRECTLY_INCURRED_OTHER_COSTS.getLabel(),
                        INDIRECT_COSTS_OTHER_COSTS.getLabel()).
                build(4);

        CostCategoryGroup costCategoryGroup = newCostCategoryGroup().
                withCostCategories(costCategories).
                build();

        CostCategoryType costCategoryType = newCostCategoryType().withCostCategoryGroup(costCategoryGroup).build();

        when(projectServiceMock.getProjectById(project.getId())).thenReturn(serviceSuccess(project));
        when(organisationServiceMock.findById(organisation.getId())).thenReturn(serviceSuccess(organisation));
        when(projectFinanceRowServiceMock.financeChecksDetails(project.getId(), organisation.getId())).thenReturn(serviceSuccess(projectFinance));
        when(competitionServiceMock.getCompetitionById(project.getCompetition())).thenReturn(serviceSuccess(competition));
        when(costCategoryTypeStrategyMock.getOrCreateCostCategoryTypeForSpendProfile(project.getId(), organisation.getId())).thenReturn(serviceSuccess(costCategoryType));

        ServiceResult<SpendProfileCostCategorySummaries> result = service.getCostCategorySummaries(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());

        SpendProfileCostCategorySummaries summaries = result.getSuccess();
        assertEquals(costCategoryType, summaries.getCostCategoryType());
        assertEquals(4, summaries.getCosts().size());

        SpendProfileCostCategorySummary summary1 = simpleFindFirst(summaries.getCosts(),
                s -> s.getCategory().equals(costCategories.get(0))).get();

        SpendProfileCostCategorySummary summary2 = simpleFindFirst(summaries.getCosts(),
                s -> s.getCategory().equals(costCategories.get(1))).get();

        SpendProfileCostCategorySummary summary3 = simpleFindFirst(summaries.getCosts(),
                s -> s.getCategory().equals(costCategories.get(2))).get();

        SpendProfileCostCategorySummary summary4 = simpleFindFirst(summaries.getCosts(),
                s -> s.getCategory().equals(costCategories.get(3))).get();

        assertEquals(new BigDecimal("6448"), summary1.getTotal());
        assertEquals(new BigDecimal("652"), summary1.getFirstMonthSpend());
        assertEquals(new BigDecimal("644"), summary1.getOtherMonthsSpend());

        assertEquals(new BigDecimal("288"), summary2.getTotal());
        assertEquals(new BigDecimal("36"), summary2.getFirstMonthSpend());
        assertEquals(new BigDecimal("28"), summary2.getOtherMonthsSpend());

        assertEquals(new BigDecimal("33"), summary3.getTotal());
        assertEquals(new BigDecimal("6"), summary3.getFirstMonthSpend());
        assertEquals(new BigDecimal("3"), summary3.getOtherMonthsSpend());

        assertEquals(new BigDecimal("98"), summary4.getTotal());
        assertEquals(new BigDecimal("17"), summary4.getFirstMonthSpend());
        assertEquals(new BigDecimal("9"), summary4.getOtherMonthsSpend());
    }

    @Override
    protected ByProjectFinanceCostCategorySummaryStrategy supplyServiceUnderTest() {
        return new ByProjectFinanceCostCategorySummaryStrategy();
    }
}
