package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionInAssessmentKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.competition.builder.CompetitionInAssessmentKeyStatisticsResourceBuilder.newCompetitionInAssessmentKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;

public class ManageAssessmentsViewModelTest {

    private static final long expectedCompetitionId = 13;
    private static final String expectedCompetitionName = "Test Competition";
    private static final CompetitionStatus expectedCompetitionStatus = CompetitionStatus.IN_ASSESSMENT;
    private static final int expectedAssignmentCount = 2;
    private static final int expectedAssignmentsWaiting = 3;
    private static final int expectedAssignmentsAccepted = 5;
    private static final int expectedAssessmentsStarted = 7;
    private static final int expectedAssessmentsSubmitted = 11;

    private ManageAssessmentsViewModel manageAssessmentsViewModel;

    @Before
    public void setUp() throws Exception {
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

        manageAssessmentsViewModel = new ManageAssessmentsViewModel(competitionResource, statisticsResource);
    }

    @Test
    public void getCompetitionId() {
        assertEquals(expectedCompetitionId, manageAssessmentsViewModel.getCompetitionId());
    }

    @Test
    public void getCompetitionName() {
        assertEquals(expectedCompetitionName, manageAssessmentsViewModel.getCompetitionName());
    }

    @Test
    public void isInAssessment() {
        assertTrue(manageAssessmentsViewModel.isInAssessment());
    }

    @Test
    public void getTotalAssessments() {
        assertEquals(expectedAssignmentCount, manageAssessmentsViewModel.getTotalAssessments());
    }

    @Test
    public void getAssessmentsAwaitingResponse() {
        assertEquals(expectedAssignmentsWaiting, manageAssessmentsViewModel.getAssessmentsAwaitingResponse());
    }

    @Test
    public void getAssessmentsAccepted() {
        assertEquals(expectedAssignmentsAccepted, manageAssessmentsViewModel.getAssessmentsAccepted());
    }

    @Test
    public void getAssessmentsCompleted() {
        assertEquals(expectedAssessmentsSubmitted, manageAssessmentsViewModel.getAssessmentsCompleted());
    }

    @Test
    public void getAssessmentsStarted() {
        assertEquals(expectedAssessmentsStarted, manageAssessmentsViewModel.getAssessmentsStarted());
    }
}