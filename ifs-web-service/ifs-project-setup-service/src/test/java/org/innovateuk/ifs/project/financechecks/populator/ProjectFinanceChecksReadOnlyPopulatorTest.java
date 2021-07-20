package org.innovateuk.ifs.project.financechecks.populator;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectFinanceChecksReadOnlyViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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

        OrganisationResource LeadOrganisation = newOrganisationResource()
                .withId(organisationId)
                .build();
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(Collections.singletonList(LeadOrganisation));

        when(projectService.getLeadOrganisation(projectId)).thenReturn(LeadOrganisation);

        CompetitionResource competitionResource = newCompetitionResource()
                .withProcurementMilestones(true)
                .build();
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(RestResult.restSuccess(competitionResource));

        ProjectFinanceChecksReadOnlyViewModel viewModel = projectFinanceChecksReadOnlyPopulator.populate(projectId);

        assertEquals(projectId, viewModel.getProjectId().longValue());
        assertTrue(viewModel.isPaymentMilestonesLink());
        assertEquals(1, viewModel.getProjectOrganisationRows().size());
        assertEquals(organisationId, viewModel.getProjectOrganisationRows().get(0).getOrganisationId().longValue());
        assertTrue(viewModel.getProjectOrganisationRows().get(0).isLead());
    }
}
