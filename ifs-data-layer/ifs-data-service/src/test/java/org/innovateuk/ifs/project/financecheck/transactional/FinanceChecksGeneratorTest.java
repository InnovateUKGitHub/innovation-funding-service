package org.innovateuk.ifs.project.financecheck.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.repository.*;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryType;
import org.innovateuk.ifs.project.financechecks.domain.CostGroup;
import org.innovateuk.ifs.project.financechecks.domain.FinanceCheck;
import org.innovateuk.ifs.project.financechecks.repository.FinanceCheckRepository;
import org.innovateuk.ifs.project.financechecks.transactional.FinanceChecksGenerator;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.spendprofile.transactional.CostCategoryTypeStrategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceRowBuilder.newApplicationFinanceRow;
import static org.innovateuk.ifs.finance.builder.FinanceRowMetaFieldBuilder.newFinanceRowMetaField;
import static org.innovateuk.ifs.finance.builder.FinanceRowMetaValueBuilder.newFinanceRowMetaValue;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceRowBuilder.newProjectFinanceRow;
import static org.innovateuk.ifs.finance.resource.OrganisationSize.SMALL;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.financecheck.builder.CostBuilder.newCost;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryGroupBuilder.newCostCategoryGroup;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryTypeBuilder.newCostCategoryType;
import static org.innovateuk.ifs.project.financecheck.builder.CostGroupBuilder.newCostGroup;
import static org.innovateuk.ifs.project.financecheck.builder.FinanceCheckBuilder.newFinanceCheck;
import static org.innovateuk.ifs.util.CollectionFunctions.zip;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FinanceChecksGeneratorTest extends BaseServiceUnitTest<FinanceChecksGenerator> {

    @Mock
    private CostCategoryTypeStrategy costCategoryTypeStrategyMock;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private FinanceCheckRepository financeCheckRepositoryMock;

    @Mock
    private ProjectFinanceRowRepository projectFinanceRowRepositoryMock;

    @Mock
    private ViabilityWorkflowHandler viabilityWorkflowHandlerMock;

    @Mock
    private ProjectFinanceRepository projectFinanceRepositoryMock;

    @Mock
    private ApplicationFinanceRepository applicationFinanceRepositoryMock;

    @Mock
    private ApplicationFinanceRowRepository applicationFinanceRowRepositoryMock;

    @Mock
    private PartnerOrganisationRepository partnerOrganisationRepositoryMock;

    @Mock
    private FinanceRowMetaValueRepository financeRowMetaValueRepositoryMock;

    @Mock
    private CompetitionService competitionService;

    private Organisation organisation;
    private List<CostCategory> costCategories;
    private CostCategoryType costCategoryTypeForOrganisation;
    private PartnerOrganisation savedProjectPartnerOrganisation;
    private Project newProject;
    private CompetitionResource competition;

    @Before
    public void setUp() {

        organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).
                build();

        competition = newCompetitionResource()
                .withIncludeJesForm(true)
                .withFundingType(FundingType.GRANT)
                .build();

        costCategories = newCostCategory().withName("Cat1", "Cat2").build(2);

        costCategoryTypeForOrganisation = newCostCategoryType().
                withCostCategoryGroup(newCostCategoryGroup().
                        withCostCategories(costCategories).
                        build()).
                build();

        savedProjectPartnerOrganisation = newPartnerOrganisation().
                withOrganisation(organisation).
                withLeadOrganisation(true).
                build();

        newProject = newProject().
                withApplication(newApplication().build()).
                withPartnerOrganisations(singletonList(savedProjectPartnerOrganisation)).
                build();

        when(projectRepositoryMock.findById(newProject.getId())).thenReturn(Optional.of(newProject));
    }

    @Test
    public void testCreateMvpFinanceChecksFigures() {

        CostGroup newFinanceCheckCostGroup = newCostGroup().
                withDescription("finance-check").
                withCosts(newCost().
                        withCostCategory(costCategories.get(0), costCategories.get(1)).
                        withValue("0.0", "0.0").
                        build(2)).
                build();

        FinanceCheck newFinanceCheck = newFinanceCheck().
                withId().
                withProject(newProject).
                withOrganisation(organisation).
                withCostGroup(newFinanceCheckCostGroup).
                build();

        ServiceResult<Void> result = service.createMvpFinanceChecksFigures(newProject, organisation, costCategoryTypeForOrganisation);
        assertTrue(result.isSuccess());

        verify(financeCheckRepositoryMock).save(createFinanceCheckExpectations(newFinanceCheck));
    }

    @Test
    public void testCreateFinanceChecksFiguresWhenOrganisationIsNotUsingJesFinances() {

        List<ProjectFinanceRow> newProjectFinanceRows = setUpCreateFinanceChecksFiguresMocking();
        ProjectFinanceRow newProjectFinanceRow1 = newProjectFinanceRows.get(0);
        ProjectFinanceRow newProjectFinanceRow2 = newProjectFinanceRows.get(1);

        when(projectFinanceRowRepositoryMock.save(createSavedProjectFinanceRowExpectation(newProjectFinanceRow1))).thenReturn(newProjectFinanceRow1);
        when(projectFinanceRowRepositoryMock.save(createSavedProjectFinanceRowExpectation(newProjectFinanceRow2))).thenReturn(newProjectFinanceRow2);

        ServiceResult<ProjectFinance> result = service.createFinanceChecksFigures(newProject, organisation);
        assertTrue(result.isSuccess());

        verify(viabilityWorkflowHandlerMock, never()).viabilityNotApplicable(Mockito.any(), Mockito.any());

        assertCreateFinanceChecksFiguresResults(newProjectFinanceRow1, newProjectFinanceRow2);
    }

    @Test
    public void testCreateFinanceChecksFiguresWhenOrganisationIsUsingJesFinances() {
        organisation.setOrganisationType(newOrganisationType().withOrganisationType(OrganisationTypeEnum.RESEARCH).build());

        PartnerOrganisation partnerOrganisation = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(newProject.getId(), organisation.getId())).thenReturn(partnerOrganisation);
        when(viabilityWorkflowHandlerMock.viabilityNotApplicable(partnerOrganisation, null)).thenReturn(true);

        List<ProjectFinanceRow> newProjectFinanceRows = setUpCreateFinanceChecksFiguresMocking();
        ProjectFinanceRow newProjectFinanceRow1 = newProjectFinanceRows.get(0);
        ProjectFinanceRow newProjectFinanceRow2 = newProjectFinanceRows.get(1);

        when(projectFinanceRowRepositoryMock.save(createSavedProjectFinanceRowExpectation(newProjectFinanceRow1))).thenReturn(newProjectFinanceRow1);
        when(projectFinanceRowRepositoryMock.save(createSavedProjectFinanceRowExpectation(newProjectFinanceRow2))).thenReturn(newProjectFinanceRow2);

        ServiceResult<ProjectFinance> result = service.createFinanceChecksFigures(newProject, organisation);
        assertTrue(result.isSuccess());

        verify(viabilityWorkflowHandlerMock).viabilityNotApplicable(partnerOrganisation, null);

        assertCreateFinanceChecksFiguresResults(newProjectFinanceRow1, newProjectFinanceRow2);
    }

    @Test
    public void createFinanceChecksWithH2020Competition() {

        PartnerOrganisation partnerOrganisation = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(newProject.getId(), organisation.getId())).thenReturn(partnerOrganisation);
        when(viabilityWorkflowHandlerMock.viabilityNotApplicable(partnerOrganisation, null)).thenReturn(true);

        List<ProjectFinanceRow> newProjectFinanceRows = setUpCreateFinanceChecksFiguresMocking();
        ProjectFinanceRow newProjectFinanceRow1 = newProjectFinanceRows.get(0);
        ProjectFinanceRow newProjectFinanceRow2 = newProjectFinanceRows.get(1);

        when(projectFinanceRowRepositoryMock.save(createSavedProjectFinanceRowExpectation(newProjectFinanceRow1))).thenReturn(newProjectFinanceRow1);
        when(projectFinanceRowRepositoryMock.save(createSavedProjectFinanceRowExpectation(newProjectFinanceRow2))).thenReturn(newProjectFinanceRow2);

        competition.setCompetitionTypeName("Horizon 2020");

        ServiceResult<ProjectFinance> result = service.createFinanceChecksFigures(newProject, organisation);
        assertTrue(result.isSuccess());

        verify(viabilityWorkflowHandlerMock).viabilityNotApplicable(partnerOrganisation, null);

        assertCreateFinanceChecksFiguresResults(newProjectFinanceRow1, newProjectFinanceRow2);

    }


    private List<ProjectFinanceRow> setUpCreateFinanceChecksFiguresMocking() {
        ApplicationFinance applicationFinance = newApplicationFinance().withOrganisationSize(SMALL).withApplication(newApplication().withCompetition(newCompetition().build()).build()).build();
        when(competitionService.getCompetitionById(applicationFinance.getApplication().getCompetition().getId())).thenReturn(serviceSuccess(competition));
        ProjectFinance newProjectFinance = new ProjectFinance(organisation, SMALL, newProject, null, null);
        newProjectFinance.setId(999L);
        List<Question> financeQuestions = newQuestion().build(2);

        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(newProject.getApplication().getId(),
                organisation.getId())).thenReturn(applicationFinance);

        when(projectFinanceRepositoryMock.save(createNewProjectFinanceExpectations(newProjectFinance))).thenReturn(newProjectFinance);

        FinanceRowMetaField financeRowMetaField1 = newFinanceRowMetaField().build();
        FinanceRowMetaField financeRowMetaField2 = newFinanceRowMetaField().build();
        FinanceRowMetaField financeRowMetaField3 = newFinanceRowMetaField().build();
        FinanceRowMetaField financeRowMetaField4 = newFinanceRowMetaField().build();

        List<ApplicationFinanceRow> originalApplicationFinanceRows = newApplicationFinanceRow().
                withOwningFinance(applicationFinance).
                withItem("An item", "An item 2").
                withCost(BigDecimal.valueOf(123L), BigDecimal.valueOf(456L)).
                withDescription("Desc 1", "Desc 2").
                withName("Name 1", "Name 2").
                withQuantity(222, 333).
                withType(FinanceRowType.FINANCE, FinanceRowType.FINANCE).
                withFinanceRowMetadata(
                        newFinanceRowMetaValue().
                                withFinanceRowMetaField(financeRowMetaField1, financeRowMetaField2).
                                withValue("Meta Value 1", "Meta Value 2").
                                build(2),
                        newFinanceRowMetaValue().
                                withFinanceRowMetaField(financeRowMetaField3, financeRowMetaField4).
                                withValue("Meta Value 3", "Meta Value 4").
                                build(2)).
                build(2);

        when(applicationFinanceRowRepositoryMock.findByTargetId(applicationFinance.getId())).
                thenReturn(originalApplicationFinanceRows);

        List<ProjectFinanceRow> newProjectFinanceRows = newProjectFinanceRow().
                withOriginalApplicationFinanceRow(originalApplicationFinanceRows.get(0), originalApplicationFinanceRows.get(1)).
                withOwningFinance(newProjectFinance).
                withItem("An item", "An item 2").
                withCost(BigDecimal.valueOf(123L), BigDecimal.valueOf(456L)).
                withDescription("Desc 1", "Desc 2").
                withName("Name 1", "Name 2").
                withQuantity(222, 333).
                withType(FinanceRowType.FINANCE, FinanceRowType.FINANCE).
                withFinanceRowMetadata(
                        newFinanceRowMetaValue().
                                withFinanceRowMetaField(financeRowMetaField1, financeRowMetaField2).
                                withValue("Meta Value 1", "Meta Value 2").
                                build(2),
                        newFinanceRowMetaValue().
                                withFinanceRowMetaField(financeRowMetaField3, financeRowMetaField4).
                                withValue("Meta Value 3", "Meta Value 4").
                                build(2)).
                build(2);

        ProjectFinanceRow newProjectFinanceRow1 = newProjectFinanceRows.get(0);
        ProjectFinanceRow newProjectFinanceRow2 = newProjectFinanceRows.get(1);

        when(projectFinanceRowRepositoryMock.save(createSavedProjectFinanceRowExpectation(newProjectFinanceRow1))).thenReturn(newProjectFinanceRow1);
        when(projectFinanceRowRepositoryMock.save(createSavedProjectFinanceRowExpectation(newProjectFinanceRow2))).thenReturn(newProjectFinanceRow2);

        return newProjectFinanceRows;

    }

    private ProjectFinanceRow createSavedProjectFinanceRowExpectation(ProjectFinanceRow expected) {
        return createLambdaMatcher(actual -> {
            assertNotNull(expected.getId());
            assertNull(actual.getId());
            assertEquals(expected.getApplicationRowId(), actual.getApplicationRowId());
            assertEquals(expected.getTarget(), actual.getTarget());
            assertEquals(expected.getCost(), actual.getCost());
            assertEquals(expected.getDescription(), actual.getDescription());
            assertEquals(expected.getItem(), actual.getItem());
            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.getQuantity(), actual.getQuantity());
            assertEquals(expected.getType(), actual.getType());
        });
    }

    private void assertCreateFinanceChecksFiguresResults(ProjectFinanceRow newProjectFinanceRow1, ProjectFinanceRow newProjectFinanceRow2) {

        verify(projectFinanceRowRepositoryMock).save(createSavedProjectFinanceRowExpectation(newProjectFinanceRow1));
        verify(financeRowMetaValueRepositoryMock).save(createSavedFinanceRowMetaValueExpectation(newProjectFinanceRow1.getFinanceRowMetadata().get(0)));
        verify(financeRowMetaValueRepositoryMock).save(createSavedFinanceRowMetaValueExpectation(newProjectFinanceRow1.getFinanceRowMetadata().get(1)));

        verify(projectFinanceRowRepositoryMock).save(createSavedProjectFinanceRowExpectation(newProjectFinanceRow2));
        verify(financeRowMetaValueRepositoryMock).save(createSavedFinanceRowMetaValueExpectation(newProjectFinanceRow2.getFinanceRowMetadata().get(0)));
        verify(financeRowMetaValueRepositoryMock).save(createSavedFinanceRowMetaValueExpectation(newProjectFinanceRow2.getFinanceRowMetadata().get(1)));
    }

    private FinanceRowMetaValue createSavedFinanceRowMetaValueExpectation(FinanceRowMetaValue original) {
        return createLambdaMatcher(copy -> {
            assertNotNull(original.getId());
            assertNull(copy.getId());
            assertEquals(original.getValue(), copy.getValue());
            assertEquals(original.getFinanceRowId(), copy.getFinanceRowId());
            assertEquals(original.getFinanceRowMetaField(), copy.getFinanceRowMetaField());
        });
    }

    private FinanceCheck createFinanceCheckExpectations(FinanceCheck expectedFinanceCheck) {
        return createLambdaMatcher(actual -> {

            assertEquals(expectedFinanceCheck.getId(), actual.getId());
            assertEquals(expectedFinanceCheck.getOrganisation(), actual.getOrganisation());
            assertEquals(expectedFinanceCheck.getProject(), actual.getProject());
            assertEquals(expectedFinanceCheck.getCostGroup().getDescription(), actual.getCostGroup().getDescription());
            assertEquals(expectedFinanceCheck.getCostGroup().getCosts().size(), actual.getCostGroup().getCosts().size());

            zip(expectedFinanceCheck.getCostGroup().getCosts(), actual.getCostGroup().getCosts(), (expectedCost, actualCost) -> {
                assertEquals(expectedCost.getCostCategory().getId(), actualCost.getCostCategory().getId());
                assertEquals(expectedCost.getValue(), actualCost.getValue());
            });
        });
    }

    private ProjectFinance createNewProjectFinanceExpectations(ProjectFinance expectedProjectFinance) {
        return createLambdaMatcher(actual -> {
            assertNotNull(expectedProjectFinance.getId());
            assertNull(actual.getId());
            assertEquals(expectedProjectFinance.getProject(), actual.getProject());
            assertEquals(expectedProjectFinance.getOrganisation(), actual.getOrganisation());
            assertEquals(expectedProjectFinance.getOrganisationSize(), actual.getOrganisationSize());
        });
    }

    @Override
    protected FinanceChecksGenerator supplyServiceUnderTest() {
        return new FinanceChecksGenerator();
    }
}
