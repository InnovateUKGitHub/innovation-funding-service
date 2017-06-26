package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionInAssessmentKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.management.viewmodel.ManageAssessmentsViewModel;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionInAssessmentKeyStatisticsResourceBuilder.newCompetitionInAssessmentKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ManageAssessmentsModelPopulatorTest extends BaseUnitTest {

    @InjectMocks
    @Spy
    private ManageAssessmentsModelPopulator manageAssessmentsModelPopulator;

    @Test
    public void populateModel() throws Exception {
        final long expectedCompetitionId = 13;
        final String expectedCompetitionName = "Test Competition";
        final CompetitionStatus expectedCompetitionStatus = CompetitionStatus.IN_ASSESSMENT;
        final int expectedAssignmentCount = 2;
        final int expectedAssignmentsWaiting = 3;
        final int expectedAssignmentsAccepted = 5;
        final int expectedAssessmentsStarted = 7;
        final int expectedAssessmentsSubmitted = 11;

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(expectedCompetitionId)
                .withName(expectedCompetitionName)
                .withCompetitionStatus(expectedCompetitionStatus)
                .build();
        CompetitionInAssessmentKeyStatisticsResource statisticsResource = newCompetitionInAssessmentKeyStatisticsResource()
                .withAssignmentCount(expectedAssignmentCount)
                .withAssignmentsWaiting(expectedAssignmentsWaiting)
                .withAssignmentsAccepted(expectedAssignmentsAccepted)
                .withAssessmentsStarted(expectedAssessmentsStarted)
                .withAssessmentsSubmitted(expectedAssessmentsSubmitted)
                .build();

        when(competitionRestService.getCompetitionById(expectedCompetitionId)).thenReturn(restSuccess(competitionResource));
        when(competitionKeyStatisticsRestServiceMock.getInAssessmentKeyStatisticsByCompetition(expectedCompetitionId)).thenReturn(restSuccess(statisticsResource));

        ManageAssessmentsViewModel expectedModel = new ManageAssessmentsViewModel(competitionResource, statisticsResource);

        ManageAssessmentsViewModel actualModel = manageAssessmentsModelPopulator.populateModel(expectedCompetitionId);

        assertEquals(expectedModel, actualModel);
    }
}