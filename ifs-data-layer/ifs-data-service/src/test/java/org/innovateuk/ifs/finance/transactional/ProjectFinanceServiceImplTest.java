package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.handler.IndustrialCostFinanceHandler;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDelegate;
import org.innovateuk.ifs.finance.handler.ProjectFinanceHandler;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.handler.item.MaterialsHandler;
import org.innovateuk.ifs.finance.mapper.ProjectFinanceMapper;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaValueRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResourceId;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.Materials;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.CLOSED;
import static org.innovateuk.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceRowBuilder.newProjectFinanceRow;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.finance.resource.OrganisationSize.SMALL;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.MATERIALS;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

public class ProjectFinanceServiceImplTest extends BaseServiceUnitTest<ProjectFinanceRowServiceImpl> {
    @Mock
    private ProjectFinanceHandler projectFinanceHandlerMock;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private IndustrialCostFinanceHandler organisationFinanceDefaultHandlerMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private OrganisationFinanceDelegate organisationFinanceDelegateMock;

    @Mock
    private ProjectFinanceRowRepository projectFinanceRowRepositoryMock;

    @Mock
    private ProjectFinanceRepository projectFinanceRepositoryMock;

    @Mock
    private ProjectFinanceMapper projectFinanceMapperMock;

    @Mock
    private FinanceRowMetaValueRepository financeRowMetaValueRepositoryMock;

    private Organisation organisation;

    private Project project;

    private ProjectFinanceRow materialCost;

    private ProjectFinance newFinance;

    private Materials material;

    @Before
    public void setUp() throws Exception {
        long organisationId = 456L;
        long projectId = 123L;
        long costItemId = 222L;
        long projectFinanceId = 111L;

        Competition competition = newCompetition().withCompetitionStatus(CLOSED).build();

        organisation = newOrganisation()
                .withId(organisationId)
                .withOrganisationType(newOrganisationType()
                        .withOrganisationType(BUSINESS)
                        .build())
                .build();

        Application application = newApplication().withCompetition(competition).build();

        project = newProject().withId(projectId).withApplication(application).build();

        newFinance = newProjectFinance()
                .withOrganisation(organisation)
                .withOrganisationSize(SMALL)
                .withProject(project)
                .build();

        newFinance.setId(projectFinanceId);

        material = newMaterials()
                .withCost(BigDecimal.valueOf(100))
                .withItem("Screws")
                .withQuantity(5)
                .withTargetId(newFinance.getId())
                .build();

        materialCost = newProjectFinanceRow()
                .withId(costItemId)
                .withType(MATERIALS)
                .withTarget(newFinance)
                .build();

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(project));

        when(organisationRepositoryMock.findById(organisation.getId())).thenReturn(Optional.of(organisation));

        when(organisationFinanceDelegateMock.getOrganisationFinanceHandler(competition.getId(), BUSINESS.getId())).thenReturn(organisationFinanceDefaultHandlerMock);

        when(projectFinanceRowRepositoryMock.findById(costItemId)).thenReturn(Optional.of(materialCost));

        when(organisationFinanceDefaultHandlerMock.toProjectDomain(material)).thenReturn(materialCost);

        when(projectFinanceRepositoryMock.findById(projectFinanceId)).thenReturn(Optional.of(newFinance));

        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(newFinance);
    }

    @Test
    public void getCostItem() {
        ServiceResult<FinanceRowItem> result = service.get(materialCost.getId());
        assertTrue(result.isSuccess());
    }

    @Test
    public void addCost() {

        ProjectFinance newFinanceExpectations = argThat(lambdaMatches(finance -> {
            assertEquals(project, finance.getProject());
            assertEquals(organisation, finance.getOrganisation());
            return true;
        }));


        ProjectFinanceResource expectedFinance = newProjectFinanceResource().
                with(id(newFinance.getId())).
                withOrganisation(organisation.getId()).
                withOrganisationSize(newFinance.getOrganisationSize()).
                withProject(project.getId()).
                build();

        when(projectFinanceRepositoryMock.save(newFinanceExpectations)).thenReturn(newFinance);
        when(projectFinanceMapperMock.mapToResource(newFinance)).thenReturn(expectedFinance);

        when(projectFinanceRowRepositoryMock.save(materialCost)).thenReturn(materialCost);

        ServiceResult<FinanceRowItem> result = service.create(material);
        assertTrue(result.isSuccess());
    }

    @Test
    public void updateCost() {
        when(projectFinanceRowRepositoryMock.save(any(ProjectFinanceRow.class))).thenReturn(materialCost);
        ServiceResult<FinanceRowItem> result = service.update(materialCost.getId(), material);
        assertTrue(result.isSuccess());
    }

    @Test
    public void deleteCost() {
        ServiceResult<Void> result = service.delete(materialCost.getId());
        assertTrue(result.isSuccess());
    }

    @Test
    public void getCostHandler() {
        when(projectFinanceRowRepositoryMock.findById(material.getId())).thenReturn(
            Optional.of(newProjectFinanceRow()
                .withType(MATERIALS)
                .withOwningFinance(newFinance)
                .build())
        );
        when(organisationFinanceDelegateMock.getOrganisationFinanceHandler(newFinance.getCompetition().getId(), newFinance.getOrganisation().getOrganisationType().getId()))
        .thenReturn(organisationFinanceDefaultHandlerMock);

        when(organisationFinanceDefaultHandlerMock.toResource(materialCost)).thenReturn(material);
        when(organisationFinanceDefaultHandlerMock.getCostHandler(MATERIALS)).thenReturn(new MaterialsHandler());

        FinanceRowHandler financeRowHandler = service.getCostHandler(material);
        assertNotNull(financeRowHandler);
        assertTrue(financeRowHandler instanceof MaterialsHandler);
    }

    @Test
    public void financeCheckDetailsSuccessful() {
        ProjectFinanceResourceId projectFinanceResourceId = new ProjectFinanceResourceId(project.getId(), organisation.getId());
        ProjectFinanceResource expected = newProjectFinanceResource().build();

        when(projectFinanceHandlerMock.getProjectOrganisationFinances(projectFinanceResourceId)).thenReturn(serviceSuccess(expected));
        ServiceResult<ProjectFinanceResource> result = service.financeChecksDetails(project.getId(), organisation.getId());

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess(), expected);
    }

    @Test
    public void financeCheckDetailsUnsuccessful() {
        ProjectFinanceResourceId projectFinanceResourceId = new ProjectFinanceResourceId(project.getId(), organisation.getId());

        when(projectFinanceHandlerMock.getProjectOrganisationFinances(projectFinanceResourceId)).thenReturn(serviceFailure(notFoundError(ProjectFinanceResource.class)));
        ServiceResult<ProjectFinanceResource> result = service.financeChecksDetails(project.getId(), organisation.getId());

        assertTrue(result.isFailure());
        assertTrue(result.getErrors().contains(notFoundError(ProjectFinanceResource.class)));
    }

    @Override
    protected ProjectFinanceRowServiceImpl supplyServiceUnderTest() {
        return new ProjectFinanceRowServiceImpl();
    }
}