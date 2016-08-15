package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.finance.domain.*;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.domain.Organisation;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.finance.resource.cost.FinanceRowType.LABOUR;
import static com.worth.ifs.finance.resource.cost.FinanceRowType.MATERIALS;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.project.finance.domain.CostTimePeriod.TimeUnit.MONTH;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ProjectFinanceServiceImplTest extends BaseServiceUnitTest<ProjectFinanceServiceImpl> {

    @Mock
    private CostCategoryTypeStrategy costCategoryTypeStrategy;

    @Mock
    private SpendProfileCostCategorySummaryStrategy spendProfileCostCategorySummaryStrategy;

    @Test
    public void testGenerateSpendProfile() {

        Long projectId = 123L;

        Project project = newProject().withId(projectId).withDuration(3L).build();
        Organisation organisation1 = newOrganisation().build();
        Organisation organisation2 = newOrganisation().build();

        CostCategory type1Cat1 = new CostCategory(LABOUR.getName());
        CostCategory type1Cat2 = new CostCategory(MATERIALS.getName());
        CostCategoryType matchingCostCategoryType1 = new CostCategoryType("Type 1", new CostCategoryGroup("Group 1", asList(type1Cat1, type1Cat2)));

        CostCategory type2Cat1 = new CostCategory(LABOUR.getName());
        CostCategoryType matchingCostCategoryType2 = new CostCategoryType("Type 2", new CostCategoryGroup("Group 2", singletonList(type2Cat1)));

        // set basic repository lookup expectations
        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        when(organisationRepositoryMock.findOne(organisation1.getId())).thenReturn(organisation1);
        when(organisationRepositoryMock.findOne(organisation2.getId())).thenReturn(organisation2);

        // setup expectations for getting project users to infer the partner organisations
        List<ProjectUserResource> projectUsers =
                newProjectUserResource().withOrganisation(organisation1.getId(), organisation2.getId()).build(2);
        when(projectServiceMock.getProjectUsers(projectId)).thenReturn(serviceSuccess(projectUsers));

        // setup expectations for finding a Cost Category Type that supports the cost categories in this Spend Profile
        when(costCategoryTypeStrategy.getOrCreateCostCategoryTypeForSpendProfile(projectId, organisation1.getId())).
                thenReturn(serviceSuccess(matchingCostCategoryType1));

        when(costCategoryTypeStrategy.getOrCreateCostCategoryTypeForSpendProfile(projectId, organisation2.getId())).
                thenReturn(serviceSuccess(matchingCostCategoryType2));

        // setup expectations for finding finance figures per Cost Category from which to generate the spend profile
        when(spendProfileCostCategorySummaryStrategy.getCostCategorySummaries(project.getId(), organisation1.getId())).thenReturn(serviceSuccess(
                asList(
                        new SpendProfileCostCategorySummary(LABOUR, new BigDecimal("100.00"), project.getDurationInMonths()),
                        new SpendProfileCostCategorySummary(MATERIALS, new BigDecimal("200.00"), project.getDurationInMonths()))));

        when(spendProfileCostCategorySummaryStrategy.getCostCategorySummaries(project.getId(), organisation2.getId())).thenReturn(serviceSuccess(
                singletonList(new SpendProfileCostCategorySummary(LABOUR, new BigDecimal("300.66"), project.getDurationInMonths()))));

        List<Cost> expectedOrganisation1EligibleCosts = asList(
                new Cost("100").withCategory(type1Cat1),
                new Cost("200").withCategory(type1Cat2));

        List<Cost> expectedOrganisation1SpendProfileFigures = asList(
                new Cost("34").withCategory(type1Cat1).withTimePeriod(0, MONTH, 1, MONTH),
                new Cost("33").withCategory(type1Cat1).withTimePeriod(1, MONTH, 1, MONTH),
                new Cost("33").withCategory(type1Cat1).withTimePeriod(2, MONTH, 1, MONTH),
                new Cost("66").withCategory(type1Cat2).withTimePeriod(0, MONTH, 1, MONTH),
                new Cost("67").withCategory(type1Cat2).withTimePeriod(1, MONTH, 1, MONTH),
                new Cost("67").withCategory(type1Cat2).withTimePeriod(2, MONTH, 1, MONTH));

        SpendProfile expectedOrganisation1Profile = new SpendProfile(organisation1, project, matchingCostCategoryType1,
                expectedOrganisation1EligibleCosts, expectedOrganisation1SpendProfileFigures);

        List<Cost> expectedOrganisation2EligibleCosts = singletonList(
                new Cost("301").withCategory(type2Cat1));

        List<Cost> expectedOrganisation2SpendProfileFigures = asList(
                new Cost("101").withCategory(type2Cat1).withTimePeriod(0, MONTH, 1, MONTH),
                new Cost("100").withCategory(type2Cat1).withTimePeriod(1, MONTH, 1, MONTH),
                new Cost("100").withCategory(type2Cat1).withTimePeriod(2, MONTH, 1, MONTH));

        SpendProfile expectedOrganisation2Profile = new SpendProfile(organisation2, project, matchingCostCategoryType2,
                expectedOrganisation2EligibleCosts, expectedOrganisation2SpendProfileFigures);

        when(spendProfileRepositoryMock.save(spendProfileExpectations(expectedOrganisation1Profile))).thenReturn(null);
        when(spendProfileRepositoryMock.save(spendProfileExpectations(expectedOrganisation2Profile))).thenReturn(null);

        ServiceResult<Void> generateResult = service.generateSpendProfile(projectId);
        assertTrue(generateResult.isSuccess());

        verify(spendProfileRepositoryMock).save(spendProfileExpectations(expectedOrganisation1Profile));
        verify(spendProfileRepositoryMock).save(spendProfileExpectations(expectedOrganisation2Profile));
        verifyNoMoreInteractions(spendProfileRepositoryMock);
    }

    private SpendProfile spendProfileExpectations(SpendProfile expectedSpendProfile) {
        return createLambdaMatcher(spendProfile -> {

            assertEquals(expectedSpendProfile.getOrganisation(), spendProfile.getOrganisation());
            assertEquals(expectedSpendProfile.getProject(), spendProfile.getProject());
            assertEquals(expectedSpendProfile.getCostCategoryType(), spendProfile.getCostCategoryType());

            CostGroup expectedEligibles = expectedSpendProfile.getEligibleCosts();
            CostGroup actualEligibles = spendProfile.getEligibleCosts();
            assertCostGroupsEqual(expectedEligibles, actualEligibles);

            CostGroup expectedSpendFigures = expectedSpendProfile.getSpendProfileFigures();
            CostGroup actualSpendFigures = spendProfile.getSpendProfileFigures();
            assertCostGroupsEqual(expectedSpendFigures, actualSpendFigures);
        });
    }

    private void assertCostGroupsEqual(CostGroup expected, CostGroup actual) {
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getCosts().size(), actual.getCosts().size());
        expected.getCosts().forEach(expectedCost ->
            assertTrue(simpleFindFirst(actual.getCosts(), actualCost -> costsMatch(expectedCost, actualCost)).isPresent())
        );
    }

    private boolean costsMatch(Cost expectedCost, Cost actualCost) {
        try {
            Optional<CostGroup> expectedCostGroup = expectedCost.getCostGroup();
            Optional<CostGroup> actualCostGroup = actualCost.getCostGroup();
            assertEquals(expectedCostGroup.isPresent(), actualCostGroup.isPresent());

            expectedCostGroup.ifPresent(expected -> {
                CostGroup actual = actualCostGroup.get();
                assertEquals(expected.getDescription(), actual.getDescription());
            });

            assertEquals(expectedCost.getValue(), actualCost.getValue());
            assertEquals(expectedCost.getCostCategory(), actualCost.getCostCategory());

            Optional<CostTimePeriod> expectedTimePeriod = expectedCost.getCostTimePeriod();
            Optional<CostTimePeriod> actualTimePeriod = actualCost.getCostTimePeriod();
            assertEquals(expectedTimePeriod.isPresent(), actualTimePeriod.isPresent());

            expectedTimePeriod.ifPresent(expected -> {
                CostTimePeriod actual = actualTimePeriod.get();
                assertEquals(expected.getOffsetAmount(), actual.getOffsetAmount());
                assertEquals(expected.getOffsetUnit(), actual.getOffsetUnit());
                assertEquals(expected.getDurationAmount(), actual.getDurationAmount());
                assertEquals(expected.getDurationUnit(), actual.getDurationUnit());
            });

            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    @Override
    protected ProjectFinanceServiceImpl supplyServiceUnderTest() {
        return new ProjectFinanceServiceImpl();
    }
}
