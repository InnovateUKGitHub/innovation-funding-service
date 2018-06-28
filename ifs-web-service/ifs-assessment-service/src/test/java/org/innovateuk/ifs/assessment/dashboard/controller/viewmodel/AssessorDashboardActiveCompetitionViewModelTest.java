package org.innovateuk.ifs.assessment.dashboard.controller.viewmodel;

import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorDashboardActiveCompetitionViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AssessorDashboardActiveCompetitionViewModelTest {

    private long competitionId;
    private long progressAssessed;
    private long progressTotal;
    private long pendingAssessments;

    @Test
    public void testNoAssessmentsForReview() {

        competitionId = 1L;
        progressAssessed = 1L;
        progressTotal = 1L;
        pendingAssessments = 0L;

        ZonedDateTime submitDeadline = ZonedDateTime.now();
        AssessorDashboardActiveCompetitionViewModel viewModel = new AssessorDashboardActiveCompetitionViewModel(
                competitionId,
                "display label",
                progressAssessed,
                progressTotal,
                pendingAssessments,
                submitDeadline.toLocalDate(),
                0,
                0);

        assertEquals((long) viewModel.getCompetitionId(), competitionId);
        assertEquals(viewModel.getProgressAssessed(), progressAssessed);
        assertEquals(viewModel.getProgressTotal(), progressTotal);
        assertEquals(viewModel.getPendingAssessments(), pendingAssessments);
        assertEquals(viewModel.hasApplicationsToAssess(), false);
        assertEquals(viewModel.hasPendingAssessments(), false);
    }

    @Test
    public void testApplicationAwaitingAcceptance() {

        competitionId = 1L;
        progressAssessed = 0L;
        progressTotal = 0L;
        pendingAssessments = 1L;

        ZonedDateTime submitDeadline = ZonedDateTime.now();
        AssessorDashboardActiveCompetitionViewModel viewModel = new AssessorDashboardActiveCompetitionViewModel(
                competitionId,
                "display label",
                progressAssessed,
                progressTotal,
                pendingAssessments,
                submitDeadline.toLocalDate(),
                0,
                0);

        assertEquals(viewModel.hasApplicationsToAssess(), false);
        assertEquals(viewModel.hasPendingAssessments(), true);
    }

    @Test
    public void testApplicationToAssess() {

        competitionId = 1L;
        progressAssessed = 1L;
        progressTotal = 0L;
        pendingAssessments = 0L;

        ZonedDateTime submitDeadline = ZonedDateTime.now();
        AssessorDashboardActiveCompetitionViewModel viewModel = new AssessorDashboardActiveCompetitionViewModel(
                competitionId,
                "display label",
                progressAssessed,
                progressTotal,
                pendingAssessments,
                submitDeadline.toLocalDate(),
                0,
                0);

        assertEquals(viewModel.hasApplicationsToAssess(), true);
        assertEquals(viewModel.hasPendingAssessments(), false);
    }

    @Test
    public void testHasAssessmentsToReviewAndApplicationsWaitingAcceptance() {

        competitionId = 1L;
        progressAssessed = 1L;
        progressTotal = 0L;
        pendingAssessments = 1L;

        ZonedDateTime submitDeadline = ZonedDateTime.now();
        AssessorDashboardActiveCompetitionViewModel viewModel = new AssessorDashboardActiveCompetitionViewModel(
                competitionId,
                "display label",
                progressAssessed,
                progressTotal,
                pendingAssessments,
                submitDeadline.toLocalDate(),
                0,
                0);

        assertEquals(viewModel.hasApplicationsToAssess(), true);
        assertEquals(viewModel.hasPendingAssessments(), true);
    }
}