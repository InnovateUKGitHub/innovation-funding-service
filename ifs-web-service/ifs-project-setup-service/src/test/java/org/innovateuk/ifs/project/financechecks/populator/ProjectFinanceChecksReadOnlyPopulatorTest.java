package org.innovateuk.ifs.project.financechecks.populator;

import org.innovateuk.ifs.application.finance.viewmodel.CostChangeViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesProjectFinancesViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesViewModel;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.eligibility.populator.ProjectFinanceChangesViewModelPopulator;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectFinanceChecksReadOnlyViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectFinanceChecksReadOnlyPopulatorTest {

    @InjectMocks
    private ProjectFinanceChecksReadOnlyPopulator projectFinanceChecksReadOnlyPopulator;

    @Mock
    private ProjectService projectService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ProjectFinanceChangesViewModelPopulator projectFinanceChangesViewModelPopulator;

    private long competitionId = 2L;
    private long projectId = 3L;
    private long organisationId = 4L;

    @Test
    public void populate() {
        ProjectResource projectResource = newProjectResource()
                .withId(projectId)
                .withCompetition(competitionId)
                .build();
        when(projectService.getById(projectId)).thenReturn(projectResource);

        OrganisationResource leadOrganisation = newOrganisationResource()
                .withId(organisationId)
                .build();
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(Collections.singletonList(leadOrganisation));

        when(projectService.getLeadOrganisation(projectId)).thenReturn(leadOrganisation);

        CompetitionResource competitionResource = newCompetitionResource()
                .withProcurementMilestones(true)
                .build();
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(RestResult.restSuccess(competitionResource));

        CostChangeViewModel costChangeViewModel = new CostChangeViewModel("ProjectCost", BigDecimal.ONE, BigDecimal.TEN);
        ProjectFinanceChangesProjectFinancesViewModel projectFinanceChangesProjectFinancesViewModel
                = new ProjectFinanceChangesProjectFinancesViewModel(Collections.emptyList(), false, false, costChangeViewModel);
        ProjectFinanceChangesViewModel projectFinanceChangesViewModel
                = new ProjectFinanceChangesViewModel(false, "", leadOrganisation.getId(), projectResource.getName(), projectResource.getApplication()
                , projectResource.getId(), false, false, null, projectFinanceChangesProjectFinancesViewModel,null);
        when(projectFinanceChangesViewModelPopulator.getProjectFinanceChangesViewModel(false, projectResource, leadOrganisation))
                .thenReturn(projectFinanceChangesViewModel);

        ProjectFinanceChecksReadOnlyViewModel viewModel = projectFinanceChecksReadOnlyPopulator.populate(projectId);

        assertEquals(projectId, viewModel.getProjectId().longValue());
        assertEquals(1, viewModel.getProjectOrganisationRows().size());
        assertEquals(organisationId, viewModel.getProjectOrganisationRows().get(0).getOrganisationId().longValue());
        assertTrue(viewModel.getProjectOrganisationRows().get(0).isLead());
        assertTrue(viewModel.getProjectOrganisationRows().get(0).isPaymentMilestonesLink());
        assertTrue(viewModel.getProjectOrganisationRows().get(0).isShowChangesLink());
    }
}
