package org.innovateuk.ifs.finance.handler;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResourceId;
import org.innovateuk.ifs.finance.resource.category.DefaultCostCategory;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static freemarker.template.utility.Collections12.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Tests for ProjectFinanceHandler methods (duh!)
 */
public class ProjectFinanceHandlerImplTest extends BaseUnitTestMocksTest {
    @InjectMocks
    private ProjectFinanceHandler handler = new ProjectFinanceHandlerImpl();

    @Mock
    private OrganisationFinanceDefaultHandler organisationFinanceDefaultHandlerMock;

    private Long projectId = 1L;

    private Long organisationId = 2L;

    //private Long projectFinanceId = 3L;

    private ProjectFinanceResourceId projectFinanceResourceId;

    private ProjectFinanceResource projectFinanceResource;

    @Before
    public void setUp() throws Exception {
        Organisation organisation = newOrganisation().withId(organisationId).withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        Competition competition = newCompetition().build();
        Application application = newApplication().withCompetition(competition).build();
        Project project = newProject().withId(projectId).withApplication(application).build();
        projectFinanceResourceId = new ProjectFinanceResourceId(projectId, organisationId);
        ProjectFinance projectFinance = newProjectFinance().withOrganisation(organisation).withProject(project).build();
        projectFinanceResource = newProjectFinanceResource().withId(projectFinance.getId()).withOrganisation(organisationId).build();
        Map<FinanceRowType, FinanceRowCostCategory> costs = new HashMap<>();
        costs.put(FinanceRowType.MATERIALS, new DefaultCostCategory());

        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectFinanceResourceId.getProjectId(), projectFinanceResourceId.getOrganisationId())).thenReturn(projectFinance);
        when(projectFinanceMapperMock.mapToResource(projectFinance)).thenReturn(projectFinanceResource);
        when(organisationRepositoryMock.findOne(organisationId)).thenReturn(organisation);
        when(organisationFinanceDelegateMock.getOrganisationFinanceHandler(anyLong())).thenReturn(organisationFinanceDefaultHandlerMock);
        when(projectFinanceRepositoryMock.findByProjectId(projectId)).thenReturn(singletonList(projectFinance));
        when(organisationFinanceDelegateMock.getOrganisationFinanceHandler(any(Long.class))).thenReturn(organisationFinanceDefaultHandlerMock);
        when(organisationFinanceDefaultHandlerMock.getProjectOrganisationFinances(projectFinance.getId())).thenReturn(costs);
        when(organisationFinanceDefaultHandlerMock.getProjectOrganisationFinanceTotals(projectFinance.getId(), competition)).thenReturn(costs);
    }

    @Test
    public void testGetResearchParticipationPercentageFromProject(){
        BigDecimal result = handler.getResearchParticipationPercentageFromProject(projectId);
        assertTrue(result != null);
    }

    @Test
    public void testGetProjectOrganisationFinances(){
        ServiceResult<ProjectFinanceResource> result = handler.getProjectOrganisationFinances(projectFinanceResourceId);
        assertTrue(result.isSuccess());
        assertEquals(result.getSuccessObject(), projectFinanceResource);
    }

    @Test
    public void testGetFinanceChecksTotals(){
        List<ProjectFinanceResource> result = handler.getFinanceChecksTotals(projectId);
        assertTrue(result.size() > 0);
    }
}
