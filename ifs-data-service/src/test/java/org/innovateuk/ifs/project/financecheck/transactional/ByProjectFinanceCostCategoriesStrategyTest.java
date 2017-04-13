package org.innovateuk.ifs.project.financecheck.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.AcademicCostCategoryGenerator;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.financecheck.domain.CostCategory;
import org.innovateuk.ifs.project.financecheck.domain.CostCategoryType;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static org.innovateuk.ifs.finance.builder.LabourCostCategoryBuilder.newLabourCostCategory;
import static org.innovateuk.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.LABOUR;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.MATERIALS;
import static org.innovateuk.ifs.project.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.builder.CostCategoryGroupBuilder.newCostCategoryGroup;
import static org.innovateuk.ifs.project.builder.CostCategoryTypeBuilder.newCostCategoryType;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.financecheck.transactional.ByProjectFinanceCostCategoriesStrategy.DESCRIPTION_PREFIX;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.user.resource.OrganisationTypeEnum.RESEARCH;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static java.util.Arrays.asList;
import static java.util.EnumSet.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ByProjectFinanceCostCategoriesStrategyTest extends BaseServiceUnitTest<ByProjectFinanceCostCategoriesStrategy> {

    @Test
    public void testIndustrialCreate() {
        // Setup
        ApplicationResource ar = newApplicationResource().build();
        ProjectResource pr = newProjectResource().withApplication(ar.getId()).build();
        OrganisationResource or = newOrganisationResource().withOrganisationType(BUSINESS.getId()).build(); // Industrial
        Map<FinanceRowType, FinanceRowCostCategory> fod = new HashMap<>();
        FinanceRowCostCategory labourFrcc = newLabourCostCategory().withCosts(newLabourCost().build(1)).build();
        FinanceRowCostCategory materialsFrcc = newDefaultCostCategory().withCosts(newMaterials().build(1)).build();
        fod.put(LABOUR, labourFrcc);
        fod.put(FinanceRowType.MATERIALS, materialsFrcc);
        ProjectFinanceResource projectFinance = newProjectFinanceResource().withFinanceOrganisationDetails(fod).build();
        CostCategoryType expectedCct = newCostCategoryType().
                withName(DESCRIPTION_PREFIX + LABOUR.getName() + ", " + MATERIALS.getName()).
                withCostCategoryGroup(newCostCategoryGroup().
                        withCostCategories(newCostCategory().
                                withName(LABOUR.getName(), MATERIALS.getName()).
                                build(2)).
                        build()).
                build();
        // Mocks
        when(projectServiceMock.getProjectById(pr.getId())).thenReturn(serviceSuccess(pr));
        when(organisationServiceMock.findById(or.getId())).thenReturn(serviceSuccess(or));
        when(projectFinanceRowServiceMock.financeChecksDetails(pr.getId(), or.getId())).thenReturn(serviceSuccess(projectFinance));
        when(costCategoryTypeRepositoryMock.findAll()).thenReturn(new ArrayList<>()); // Force a create code execution
        when(costCategoryTypeRepositoryMock.save(matcherForCostCategoryType(expectedCct))).thenReturn(expectedCct);
        // Method under test
        ServiceResult<CostCategoryType> result = service.getOrCreateCostCategoryTypeForSpendProfile(pr.getId(), or.getId());
        assertTrue(result.isSuccess());
        assertEquals(expectedCct, result.getSuccessObject()); // We matched
    }


    @Test
    public void testAlreadyCreated() {
        // Setup
        ApplicationResource ar = newApplicationResource().build();
        ProjectResource pr = newProjectResource().withApplication(ar.getId()).build();
        OrganisationResource or = newOrganisationResource().withOrganisationType(RESEARCH.getId()).build(); // Academic
        ProjectFinanceResource projectFinance = newProjectFinanceResource().build();
        CostCategoryType expectedCct = newCostCategoryType().
                withName("A name that will not match - we care only about the contained CostCategories").
                withCostCategoryGroup(newCostCategoryGroup().
                        withCostCategories(newCostCategory().
                                withName(simpleMapArray(AcademicCostCategoryGenerator.values(), AcademicCostCategoryGenerator::getName, String.class)).
                                withLabel(simpleMapArray(AcademicCostCategoryGenerator.values(), AcademicCostCategoryGenerator::getLabel, String.class)).
                                build(AcademicCostCategoryGenerator.values().length)).
                        build()).
                build();
        // Mocks
        when(projectServiceMock.getProjectById(pr.getId())).thenReturn(serviceSuccess(pr));
        when(organisationServiceMock.findById(or.getId())).thenReturn(serviceSuccess(or));
        when(projectFinanceRowServiceMock.financeChecksDetails(pr.getId(), or.getId())).thenReturn(serviceSuccess(projectFinance));
        when(costCategoryTypeRepositoryMock.findAll()).thenReturn(asList(expectedCct)); // This is the one already created and should be returned
        // Method under test
        ServiceResult<CostCategoryType> result = service.getOrCreateCostCategoryTypeForSpendProfile(pr.getId(), or.getId());
        assertTrue(result.isSuccess());
        assertEquals(expectedCct, result.getSuccessObject()); // We matched
    }

    @Test
    public void testAcademicCreate() {
        // Setup
        ApplicationResource ar = newApplicationResource().build();
        ProjectResource pr = newProjectResource().withApplication(ar.getId()).build();
        OrganisationResource or = newOrganisationResource().withOrganisationType(RESEARCH.getId()).build(); // Academic
        ProjectFinanceResource projectFinance = newProjectFinanceResource().build();
        CostCategoryType expectedCct = newCostCategoryType().
                withName(DESCRIPTION_PREFIX + simpleJoiner(sorted(allOf(AcademicCostCategoryGenerator.class)), AcademicCostCategoryGenerator::getName, ", ")).
                withCostCategoryGroup(newCostCategoryGroup().
                        withCostCategories(newCostCategory().
                                withName(simpleMapArray(AcademicCostCategoryGenerator.values(), AcademicCostCategoryGenerator::getName, String.class)).
                                withLabel(simpleMapArray(AcademicCostCategoryGenerator.values(), AcademicCostCategoryGenerator::getLabel, String.class)).
                                build(AcademicCostCategoryGenerator.values().length)).
                        build()).
                build();
        // Mocks
        when(projectServiceMock.getProjectById(pr.getId())).thenReturn(serviceSuccess(pr));
        when(organisationServiceMock.findById(or.getId())).thenReturn(serviceSuccess(or));
        when(projectFinanceRowServiceMock.financeChecksDetails(pr.getId(), or.getId())).thenReturn(serviceSuccess(projectFinance));
        when(costCategoryTypeRepositoryMock.findAll()).thenReturn(new ArrayList<>()); // Force a create code execution
        when(costCategoryTypeRepositoryMock.save(matcherForCostCategoryType(expectedCct))).thenReturn(expectedCct);
        // Method under test
        ServiceResult<CostCategoryType> result = service.getOrCreateCostCategoryTypeForSpendProfile(pr.getId(), or.getId());
        assertTrue(result.isSuccess());
        assertEquals(expectedCct, result.getSuccessObject()); // We matched
    }

    private CostCategoryType matcherForCostCategoryType(CostCategoryType expected) {
        return createLambdaMatcher(actual -> {
            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.getCostCategories().size(), expected.getCostCategories().size());
            assertTrue(containsAll(expected.getCostCategories(), CostCategory::getName, actual.getCostCategories(), CostCategory::getName));
            assertTrue(containsAll(expected.getCostCategories(), CostCategory::getLabel, actual.getCostCategories(), CostCategory::getLabel));
        });
    }

    @Override
    protected ByProjectFinanceCostCategoriesStrategy supplyServiceUnderTest() {
        return new ByProjectFinanceCostCategoriesStrategy();
    }
}
