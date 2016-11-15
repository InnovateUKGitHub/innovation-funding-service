package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.category.FinanceRowCostCategory;
import com.worth.ifs.finance.resource.cost.AcademicCostCategoryGenerator;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import com.worth.ifs.project.finance.domain.CostCategory;
import com.worth.ifs.project.finance.domain.CostCategoryType;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static com.worth.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static com.worth.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static com.worth.ifs.finance.builder.LabourCostCategoryBuilder.newLabourCostCategory;
import static com.worth.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static com.worth.ifs.finance.resource.cost.FinanceRowType.LABOUR;
import static com.worth.ifs.finance.resource.cost.FinanceRowType.MATERIALS;
import static com.worth.ifs.project.builder.CostCategoryBuilder.newCostCategory;
import static com.worth.ifs.project.builder.CostCategoryGroupBuilder.newCostCategoryGroup;
import static com.worth.ifs.project.builder.CostCategoryTypeBuilder.newCostCategoryType;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.finance.transactional.ByApplicationFinanceCostCategoriesStrategy.DESCRIPTION_PREFIX;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.resource.OrganisationTypeEnum.BUSINESS;
import static com.worth.ifs.user.resource.OrganisationTypeEnum.RESEARCH;
import static com.worth.ifs.util.CollectionFunctions.*;
import static java.util.Arrays.asList;
import static java.util.EnumSet.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ByApplicationFinanceCostCategoriesStrategyTest extends BaseServiceUnitTest<ByApplicationFinanceCostCategoriesStrategy> {

    @Test
    public void testIndustrialCreate() {
        // Setup
        ApplicationResource ar = newApplicationResource().build();
        ProjectResource pr = newProjectResource().withApplication(ar.getId()).build();
        OrganisationResource or = newOrganisationResource().withOrganisationType(BUSINESS.getOrganisationTypeId()).build(); // Industrial
        Map<FinanceRowType, FinanceRowCostCategory> fod = new HashMap<>();
        FinanceRowCostCategory labourFrcc = newLabourCostCategory().withCosts(newLabourCost().build(1)).build();
        FinanceRowCostCategory materialsFrcc = newDefaultCostCategory().withCosts(newMaterials().build(1)).build();
        fod.put(LABOUR, labourFrcc);
        fod.put(FinanceRowType.MATERIALS, materialsFrcc);
        ApplicationFinanceResource afr = newApplicationFinanceResource().withFinanceOrganisationDetails(fod).build();
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
        when(financeRowServiceMock.financeDetails(ar.getId(), or.getId())).thenReturn(serviceSuccess(afr));
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
        OrganisationResource or = newOrganisationResource().withOrganisationType(RESEARCH.getOrganisationTypeId()).build(); // Academic
        ApplicationFinanceResource afr = newApplicationFinanceResource().build();
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
        when(financeRowServiceMock.financeDetails(ar.getId(), or.getId())).thenReturn(serviceSuccess(afr));
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
        OrganisationResource or = newOrganisationResource().withOrganisationType(RESEARCH.getOrganisationTypeId()).build(); // Academic
        ApplicationFinanceResource afr = newApplicationFinanceResource().build();
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
        when(financeRowServiceMock.financeDetails(ar.getId(), or.getId())).thenReturn(serviceSuccess(afr));
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
    protected ByApplicationFinanceCostCategoriesStrategy supplyServiceUnderTest() {
        return new ByApplicationFinanceCostCategoriesStrategy();
    }
}
