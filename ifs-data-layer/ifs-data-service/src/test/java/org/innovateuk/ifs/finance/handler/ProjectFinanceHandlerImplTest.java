package org.innovateuk.ifs.finance.handler;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.mapper.ProjectFinanceMapper;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResourceId;
import org.innovateuk.ifs.finance.resource.category.DefaultCostCategory;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryType;
import org.innovateuk.ifs.project.financechecks.transactional.FinanceChecksGenerator;
import org.innovateuk.ifs.project.spendprofile.transactional.CostCategoryTypeStrategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.MATERIALS;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryGroupBuilder.newCostCategoryGroup;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryTypeBuilder.newCostCategoryType;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectFinanceHandlerImplTest extends BaseUnitTestMocksTest {
    @InjectMocks
    private ProjectFinanceHandler handler = new ProjectFinanceHandlerImpl();

    @Mock
    private IndustrialCostFinanceHandler organisationFinanceDefaultHandlerMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private OrganisationFinanceDelegate organisationFinanceDelegateMock;

    @Mock
    private ProjectFinanceRepository projectFinanceRepositoryMock;

    @Mock
    private ProjectFinanceMapper projectFinanceMapperMock;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CostCategoryTypeStrategy costCategoryTypeStrategy;

    @Mock
    private FinanceChecksGenerator financeChecksGenerator;

    private long projectId = 1L;

    private Organisation organisation;
    private Project project;
    private ProjectFinance projectFinance;

    private ProjectFinanceResourceId projectFinanceResourceId;

    private ProjectFinanceResource projectFinanceResource;

    @Before
    public void setUp() throws Exception {
        long organisationId = 2L;

        organisation = newOrganisation()
                .withId(organisationId)
                .withOrganisationType(BUSINESS)
                .build();

        Competition competition = newCompetition().build();
        Application application = newApplication().withCompetition(competition).build();

         project = newProject()
                .withId(projectId)
                .withApplication(application)
                .withPartnerOrganisations(newPartnerOrganisation().withOrganisation(organisation).build(1))
                .build();

        projectFinanceResourceId = new ProjectFinanceResourceId(projectId, organisationId);
        projectFinance = newProjectFinance()
                .withOrganisation(organisation)
                .withProject(project)
                .build();

        projectFinanceResource = newProjectFinanceResource()
                .withId(projectFinance.getId())
                .withOrganisation(organisationId)
                .build();

        Map<FinanceRowType, FinanceRowCostCategory> costs = new HashMap<>();
        costs.put(MATERIALS, new DefaultCostCategory());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectFinanceResourceId.getProjectId(), projectFinanceResourceId.getOrganisationId())).thenReturn(Optional.of(projectFinance));
        when(projectFinanceMapperMock.mapToResource(projectFinance)).thenReturn(projectFinanceResource);
        when(organisationRepositoryMock.findById(organisationId)).thenReturn(Optional.of(organisation));
        when(organisationFinanceDelegateMock.getOrganisationFinanceHandler(anyLong(), anyLong())).thenReturn(organisationFinanceDefaultHandlerMock);
        when(projectFinanceRepositoryMock.findByProjectId(projectId)).thenReturn(singletonList(projectFinance));
        when(organisationFinanceDelegateMock.getOrganisationFinanceHandler(anyLong(), any(Long.class))).thenReturn(organisationFinanceDefaultHandlerMock);
        when(organisationFinanceDefaultHandlerMock.getProjectOrganisationFinances(projectFinance.getId())).thenReturn(costs);
        when(organisationFinanceDefaultHandlerMock.getProjectOrganisationFinances(projectFinance.getId())).thenReturn(costs);
    }

    @Test
    public void getResearchParticipationPercentageFromProject() {
        BigDecimal result = handler.getResearchParticipationPercentageFromProject(projectId);
        assertNotNull(result);
    }

    @Test
    public void getProjectOrganisationFinances() {
        ServiceResult<ProjectFinanceResource> result = handler.getProjectOrganisationFinances(projectFinanceResourceId);
        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess(), projectFinanceResource);
    }

    @Test
    public void getFinanceChecksTotals() {
        List<ProjectFinanceResource> result = handler.getFinanceChecksTotals(projectId);
        assertTrue(result.size() > 0);
    }

    @Test
    public void getFinanceChecksTotals_willCreateFinances() {
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectFinanceResourceId.getProjectId(), projectFinanceResourceId.getOrganisationId())).thenReturn(Optional.empty());

        CostCategoryType costCategoryTypeForOrganisation = newCostCategoryType().
                withCostCategoryGroup(newCostCategoryGroup().
                        withCostCategories(newCostCategory().withName("Cat1", "Cat2").build(2)).
                        build()).
                build();

        when(costCategoryTypeStrategy.getOrCreateCostCategoryTypeForSpendProfile(projectFinanceResourceId.getProjectId(),
                projectFinanceResourceId.getOrganisationId())).thenReturn(serviceSuccess(costCategoryTypeForOrganisation));
        when(financeChecksGenerator.createMvpFinanceChecksFigures(project, organisation, costCategoryTypeForOrganisation)).thenReturn(serviceSuccess());
        when(financeChecksGenerator.createFinanceChecksFigures(project, organisation)).thenReturn(serviceSuccess(projectFinance));

        List<ProjectFinanceResource> result = handler.getFinanceChecksTotals(projectId);
        assertTrue(result.size() > 0);

        verify(costCategoryTypeStrategy).getOrCreateCostCategoryTypeForSpendProfile(projectFinanceResourceId.getProjectId(),
                projectFinanceResourceId.getOrganisationId());
        verify(financeChecksGenerator).createMvpFinanceChecksFigures(project, organisation, costCategoryTypeForOrganisation);
        verify(financeChecksGenerator).createFinanceChecksFigures(project, organisation);

    }
}
