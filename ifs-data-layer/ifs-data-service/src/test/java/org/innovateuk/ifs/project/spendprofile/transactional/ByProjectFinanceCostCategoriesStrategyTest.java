package org.innovateuk.ifs.project.spendprofile.transactional;

import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static org.innovateuk.ifs.finance.builder.LabourCostCategoryBuilder.newLabourCostCategory;
import static org.innovateuk.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.LABOUR;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.MATERIALS;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.RESEARCH;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryGroupBuilder.newCostCategoryGroup;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryTypeBuilder.newCostCategoryType;
import static org.innovateuk.ifs.project.spendprofile.transactional.ByProjectFinanceCostCategoriesStrategy.DESCRIPTION_PREFIX;
import static org.innovateuk.ifs.util.CollectionFunctions.containsAll;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.AcademicCostCategoryGenerator;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryType;
import org.innovateuk.ifs.project.financechecks.repository.CostCategoryTypeRepository;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.mockito.Mock;

    public class ByProjectFinanceCostCategoriesStrategyTest extends BaseServiceUnitTest<ByProjectFinanceCostCategoriesStrategy> {

    @Mock
    private ProjectService projectServiceMock;

    @Mock
    private OrganisationService organisationServiceMock;

    @Mock
    private ProjectFinanceService projectFinanceService;

    @Mock
    private CostCategoryTypeRepository costCategoryTypeRepositoryMock;

    @Mock
    private CompetitionService competitionService;

    @Test
    public void testIndustrialCreate() {
        CompetitionResource competition = newCompetitionResource().withIncludeJesForm(true).withFundingType(FundingType.GRANT).build();
        ApplicationResource ar = newApplicationResource().build();
        ProjectResource pr = newProjectResource().withCompetition(competition.getId()).withApplication(ar.getId()).build();
        OrganisationResource or = newOrganisationResource().withOrganisationType(BUSINESS.getId()).build(); // Industrial
        Map<FinanceRowType, FinanceRowCostCategory> fod = new HashMap<>();
        FinanceRowCostCategory labourFrcc = newLabourCostCategory().withCosts(newLabourCost().build(1)).build();
        FinanceRowCostCategory materialsFrcc = newDefaultCostCategory().withCosts(newMaterials().build(1)).build();
        fod.put(LABOUR, labourFrcc);
        fod.put(FinanceRowType.MATERIALS, materialsFrcc);
        ProjectFinanceResource projectFinance = newProjectFinanceResource().withFinanceOrganisationDetails(fod).build();
        CostCategoryType expectedCct = newCostCategoryType().
                withName(DESCRIPTION_PREFIX + LABOUR.getDisplayName() + ", " + MATERIALS.getDisplayName()).
                withCostCategoryGroup(newCostCategoryGroup().
                        withCostCategories(newCostCategory().
                                withName(LABOUR.getDisplayName(), MATERIALS.getDisplayName()).
                                build(2)).
                        build()).
                build();

        when(projectServiceMock.getProjectById(pr.getId())).thenReturn(serviceSuccess(pr));
        when(organisationServiceMock.findById(or.getId())).thenReturn(serviceSuccess(or));
        when(projectFinanceService.financeChecksDetails(pr.getId(), or.getId())).thenReturn(serviceSuccess(projectFinance));
        when(costCategoryTypeRepositoryMock.findAll()).thenReturn(new ArrayList<>()); // Force a create code execution
        when(costCategoryTypeRepositoryMock.save(matcherForCostCategoryType(expectedCct))).thenReturn(expectedCct);
        when(competitionService.getCompetitionById(competition.getId())).thenReturn(serviceSuccess(competition));

        CostCategoryType result = service.getOrCreateCostCategoryTypeForSpendProfile(pr.getId(), or.getId()).getSuccess();

        assertEquals(expectedCct, result);
    }


    @Test
    public void testAlreadyCreated() {
        CompetitionResource competition = newCompetitionResource().withIncludeJesForm(true).withFundingType(FundingType.GRANT).build();
        ApplicationResource ar = newApplicationResource().build();
        ProjectResource pr = newProjectResource().withCompetition(competition.getId()).withApplication(ar.getId()).build();
        OrganisationResource or = newOrganisationResource().withOrganisationType(RESEARCH.getId()).build(); // Academic
        ProjectFinanceResource projectFinance = newProjectFinanceResource().build();

        AcademicCostCategoryGenerator[] spendProfileGenerators =
                stream(AcademicCostCategoryGenerator.values())
                        .filter(AcademicCostCategoryGenerator::isIncludedInSpendProfile)
                        .toArray(AcademicCostCategoryGenerator[]::new);

        CostCategoryType expectedCct = newCostCategoryType().
                withName("A name that will not match - we care only about the contained CostCategories").
                withCostCategoryGroup(newCostCategoryGroup().
                        withCostCategories(newCostCategory().
                                withName(simpleMapArray(spendProfileGenerators, AcademicCostCategoryGenerator::getDisplayName, String.class)).
                                withLabel(simpleMapArray(spendProfileGenerators, AcademicCostCategoryGenerator::getLabel, String.class)).
                                build(spendProfileGenerators.length)).
                        build()).
                build();

        when(projectServiceMock.getProjectById(pr.getId())).thenReturn(serviceSuccess(pr));
        when(organisationServiceMock.findById(or.getId())).thenReturn(serviceSuccess(or));
        when(projectFinanceService.financeChecksDetails(pr.getId(), or.getId())).thenReturn(serviceSuccess(projectFinance));
        when(costCategoryTypeRepositoryMock.findAll()).thenReturn(singletonList(expectedCct)); // This is the one already created and should be returned
        when(competitionService.getCompetitionById(competition.getId())).thenReturn(serviceSuccess(competition));

        CostCategoryType result = service.getOrCreateCostCategoryTypeForSpendProfile(pr.getId(), or.getId()).getSuccess();

        verify(projectServiceMock).getProjectById((anyLong()));
        verify(organisationServiceMock).findById(anyLong());
        verify(projectFinanceService).financeChecksDetails(anyLong(), anyLong());
        verify(costCategoryTypeRepositoryMock).findAll();
        verifyNoMoreInteractions(costCategoryTypeRepositoryMock);

        assertEquals(expectedCct, result);
    }

    @Test
    public void testAcademicCreate() {
        CompetitionResource competition = newCompetitionResource().withIncludeJesForm(true).withFundingType(FundingType.GRANT).build();
        ApplicationResource ar = newApplicationResource().build();
        ProjectResource pr = newProjectResource().withCompetition(competition.getId()).withApplication(ar.getId()).build();
        OrganisationResource or = newOrganisationResource().withOrganisationType(RESEARCH.getId()).build(); // Academic
        ProjectFinanceResource projectFinance = newProjectFinanceResource().build();

        AcademicCostCategoryGenerator[] spendProfileGenerators =
                stream(AcademicCostCategoryGenerator.values())
                        .filter(AcademicCostCategoryGenerator::isIncludedInSpendProfile)
                        .toArray(AcademicCostCategoryGenerator[]::new);

        CostCategoryType expectedCct = newCostCategoryType().
                withName(DESCRIPTION_PREFIX + simpleJoiner(simpleFilter(AcademicCostCategoryGenerator.values(),
                                                                        AcademicCostCategoryGenerator::isIncludedInSpendProfile),
                                                           AcademicCostCategoryGenerator::getDisplayName,
                                                           ", ")).
                withCostCategoryGroup(newCostCategoryGroup().
                        withCostCategories(newCostCategory().
                                withName(simpleMapArray(spendProfileGenerators, AcademicCostCategoryGenerator::getDisplayName, String.class)).
                                withLabel(simpleMapArray(spendProfileGenerators, AcademicCostCategoryGenerator::getLabel, String.class)).
                                build(spendProfileGenerators.length)).
                        build()).
                build();

        when(projectServiceMock.getProjectById(pr.getId())).thenReturn(serviceSuccess(pr));
        when(organisationServiceMock.findById(or.getId())).thenReturn(serviceSuccess(or));
        when(projectFinanceService.financeChecksDetails(pr.getId(), or.getId())).thenReturn(serviceSuccess(projectFinance));
        when(costCategoryTypeRepositoryMock.findAll()).thenReturn(new ArrayList<>()); // Force a create code execution
        when(costCategoryTypeRepositoryMock.save(matcherForCostCategoryType(expectedCct))).thenReturn(expectedCct);
        when(competitionService.getCompetitionById(competition.getId())).thenReturn(serviceSuccess(competition));

        CostCategoryType result = service.getOrCreateCostCategoryTypeForSpendProfile(pr.getId(), or.getId()).getSuccess();
        assertEquals(expectedCct, result);
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