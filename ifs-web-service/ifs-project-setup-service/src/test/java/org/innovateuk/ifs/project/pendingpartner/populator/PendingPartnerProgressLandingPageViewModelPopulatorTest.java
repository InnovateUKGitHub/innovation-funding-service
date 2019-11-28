package org.innovateuk.ifs.project.pendingpartner.populator;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.pendingpartner.viewmodel.PendingPartnerProgressLandingPageViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.RESEARCH;
import static org.innovateuk.ifs.project.builder.PendingPartnerProgressResourceBuilder.newPendingPartnerProgressResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PendingPartnerProgressLandingPageViewModelPopulatorTest {

    @InjectMocks
    private PendingPartnerProgressLandingPageViewModelPopulator populator;

    @Mock
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Test
    public void populate() {
        long projectId = 1L;
        long organisationId = 2L;

        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.GRANT)
                .withIncludeJesForm(true)
                .build();
        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withName("proj")
                .withApplication(3L)
                .withCompetition(competition.getId()).build();
        PendingPartnerProgressResource progress = newPendingPartnerProgressResource()
                .withYourFundingCompletedOn(ZonedDateTime.now())
                .withYourOrganisationCompletedOn(ZonedDateTime.now())
                .withTermsAndConditionsCompletedOn(ZonedDateTime.now())
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withOrganisationType(RESEARCH.getId())
                .build();

        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        when(pendingPartnerProgressRestService.getPendingPartnerProgress(projectId, organisationId)).thenReturn(restSuccess(progress));
        when(competitionRestService.getCompetitionById(project.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisation));

        PendingPartnerProgressLandingPageViewModel viewModel = populator.populate(projectId, organisationId);

        assertEquals("proj", viewModel.getProjectName());
        assertEquals(3L, viewModel.getApplicationId());
        assertEquals(projectId, viewModel.getProjectId());
        assertFalse( viewModel.isShowYourOrganisation());
        assertTrue(viewModel.isTermsAndConditionsComplete());
        assertTrue(viewModel.isYourFundingComplete());
        assertTrue(viewModel.isYourOrganisationComplete());
    }
}
