package org.innovateuk.ifs.management.competition.setup.closecompetition.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.competition.setup.closecompetition.viewmodel.AlwaysOpenCloseCompetitionViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.innovateuk.ifs.competition.resource.CompetitionCompletionStage.PROJECT_SETUP;
import static org.innovateuk.ifs.competition.resource.MilestoneType.SUBMISSION_DATE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AlwaysOpenCloseCompetitionViewModelPopulatorTest {

    @InjectMocks
    private AlwaysOpenCloseCompetitionViewModelPopulator populator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private MilestoneRestService milestoneRestService;

    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Test
    public void populate() {
        long competitionId = 100L;
        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withName("Always open competition")
                .withAlwaysOpen(true)
                .withCompletionStage(PROJECT_SETUP)
                .build();
        MilestoneResource submissionDate = newMilestoneResource()
                .withCompetitionId(competitionId)
                .withName(SUBMISSION_DATE)
                .withDate(now())
                .build();
        List<ApplicationResource> applications = newApplicationResource()
                .withId(23L, 24L)
                .withCompetition(competitionId)
                .withApplicationState(SUBMITTED)
                .build(2);

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(milestoneRestService.getMilestoneByTypeAndCompetitionId(SUBMISSION_DATE, competitionId)).thenReturn(restSuccess(submissionDate));
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(competitionId, empty(), empty(), empty()))
                .thenReturn(restSuccess(asList(applications.get(0).getId(),applications.get(1).getId())));
        when(applicationRestService.getApplicationById(23L)).thenReturn(restSuccess(applications.get(0)));
        when(applicationRestService.getApplicationById(24L)).thenReturn(restSuccess(applications.get(1)));

        AlwaysOpenCloseCompetitionViewModel viewModel = populator.populate(competitionId);

        assertEquals(competition.getName(), viewModel.getCompetitionName());
        assertEquals(submissionDate.getDate(), viewModel.getSubmissionDate());
        assertEquals(2, viewModel.getSubmittedApplications().size());
    }
}