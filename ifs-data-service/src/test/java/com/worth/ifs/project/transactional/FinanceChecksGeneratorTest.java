package com.worth.ifs.project.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.domain.*;
import com.worth.ifs.project.domain.PartnerOrganisation;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.finance.domain.CostCategory;
import com.worth.ifs.project.finance.domain.CostCategoryType;
import com.worth.ifs.project.finance.domain.CostGroup;
import com.worth.ifs.project.finance.domain.FinanceCheck;
import com.worth.ifs.project.finance.transactional.CostCategoryTypeStrategy;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationSize;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;

import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static com.worth.ifs.finance.builder.ApplicationFinanceRowBuilder.newApplicationFinanceRow;
import static com.worth.ifs.finance.builder.FinanceRowMetaFieldBuilder.newFinanceRowMetaField;
import static com.worth.ifs.finance.builder.FinanceRowMetaValueBuilder.newFinanceRowMetaValue;
import static com.worth.ifs.finance.builder.ProjectFinanceRowBuilder.newProjectFinanceRow;
import static com.worth.ifs.project.builder.CostBuilder.newCost;
import static com.worth.ifs.project.builder.CostCategoryBuilder.newCostCategory;
import static com.worth.ifs.project.builder.CostCategoryGroupBuilder.newCostCategoryGroup;
import static com.worth.ifs.project.builder.CostCategoryTypeBuilder.newCostCategoryType;
import static com.worth.ifs.project.builder.CostGroupBuilder.newCostGroup;
import static com.worth.ifs.project.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.finance.builder.FinanceCheckBuilder.newFinanceCheck;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.util.CollectionFunctions.zip;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FinanceChecksGeneratorTest extends BaseServiceUnitTest<FinanceChecksGenerator> {

    @Mock
    private CostCategoryTypeStrategy costCategoryTypeStrategyMock;

    private Organisation organisation;
    private List<CostCategory> costCategories;
    private CostCategoryType costCategoryTypeForOrganisation;
    private PartnerOrganisation savedProjectPartnerOrganisation;
    private Project newProject;

    @Before
    public void setUp() {

        organisation = newOrganisation().
                withOrganisationSize(OrganisationSize.MEDIUM).
                withOrganisationType(OrganisationTypeEnum.BUSINESS).
                build();

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

        when(projectRepositoryMock.findOne(newProject.getId())).thenReturn(newProject);
    }

    @Test
    public void testCreateMvpFinanceChecksFigures() {

        CostGroup newFinanceCheckCostGroup = newCostGroup().
                withDescription("finance-check").
                withCosts(newCost().
                        withCostCategory(costCategories.get(0), costCategories.get(1)).
                        withValue("0", "0").
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
    public void testCreateFinanceChecksFigures() {

        ApplicationFinance applicationFinance = newApplicationFinance().withOrganisationSize(organisation.getOrganisationSize()).build();
        ProjectFinance newProjectFinance = new ProjectFinance(organisation, organisation.getOrganisationSize(), newProject);
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
                withQuestion(financeQuestions.get(0), financeQuestions.get(1)).
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
                withQuestion(financeQuestions.get(0), financeQuestions.get(1)).
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

        ServiceResult<Void> result = service.createFinanceChecksFigures(newProject, organisation);
        assertTrue(result.isSuccess());

        verify(projectFinanceRowRepositoryMock).save(createSavedProjectFinanceRowExpectation(newProjectFinanceRow1));
        verify(financeRowMetaValueRepositoryMock).save(createSavedFinanceRowMetaValueExpectation(newProjectFinanceRow1.getFinanceRowMetadata().get(0)));
        verify(financeRowMetaValueRepositoryMock).save(createSavedFinanceRowMetaValueExpectation(newProjectFinanceRow1.getFinanceRowMetadata().get(1)));

        verify(projectFinanceRowRepositoryMock).save(createSavedProjectFinanceRowExpectation(newProjectFinanceRow2));
        verify(financeRowMetaValueRepositoryMock).save(createSavedFinanceRowMetaValueExpectation(newProjectFinanceRow2.getFinanceRowMetadata().get(0)));
        verify(financeRowMetaValueRepositoryMock).save(createSavedFinanceRowMetaValueExpectation(newProjectFinanceRow2.getFinanceRowMetadata().get(1)));
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
            assertEquals(expected.getQuestion(), actual.getQuestion());
        });
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
