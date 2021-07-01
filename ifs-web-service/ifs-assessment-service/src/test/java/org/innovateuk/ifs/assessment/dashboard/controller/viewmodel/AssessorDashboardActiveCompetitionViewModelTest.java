package org.innovateuk.ifs.assessment.dashboard.controller.viewmodel;

import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorDashboardActiveCompetitionViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AssessorDashboardActiveCompetitionViewModelTest {

    private long competitionId;
    private long progressAssessed;
    private long progressTotal;
    private long pendingAssessments;
    private long batchIndex;

    @Test
    public void testNoAssessmentsForReview() {

        competitionId = 1L;
        progressAssessed = 1L;
        progressTotal = 1L;
        pendingAssessments = 0L;
        batchIndex = 0L;

        ZonedDateTime submitDeadline = ZonedDateTime.now();
        AssessorDashboardActiveCompetitionViewModel viewModel = new AssessorDashboardActiveCompetitionViewModel(
                competitionId,
                "display label",
                progressAssessed,
                progressTotal,
                pendingAssessments,
                submitDeadline.toLocalDate(),
                0,
                0,
                false,
                batchIndex);

        assertEquals(competitionId, (long) viewModel.getCompetitionId());
        assertEquals(progressAssessed, viewModel.getProgressAssessed());
        assertEquals(progressTotal, viewModel.getProgressTotal());
        assertEquals(pendingAssessments, viewModel.getPendingAssessments());
        assertEquals(false, viewModel.hasApplicationsToAssess());
        assertEquals(false, viewModel.hasPendingAssessments());
        assertEquals(batchIndex, viewModel.getBatchIndex());
    }

    @Test
    public void testApplicationAwaitingAcceptance() {

        competitionId = 1L;
        progressAssessed = 0L;
        progressTotal = 0L;
        pendingAssessments = 1L;
        batchIndex = 1L;

        ZonedDateTime submitDeadline = ZonedDateTime.now();
        AssessorDashboardActiveCompetitionViewModel viewModel = new AssessorDashboardActiveCompetitionViewModel(
                competitionId,
                "display label",
                progressAssessed,
                progressTotal,
                pendingAssessments,
                submitDeadline.toLocalDate(),
                0,
                0,
                false,
                batchIndex);

        assertEquals(false, viewModel.hasApplicationsToAssess());
        assertEquals(true, viewModel.hasPendingAssessments());
        assertEquals(batchIndex, viewModel.getBatchIndex());
    }

    @Test
    public void testApplicationToAssess() {

        competitionId = 1L;
        progressAssessed = 1L;
        progressTotal = 0L;
        pendingAssessments = 0L;
        batchIndex = 1L;

        ZonedDateTime submitDeadline = ZonedDateTime.now();
        AssessorDashboardActiveCompetitionViewModel viewModel = new AssessorDashboardActiveCompetitionViewModel(
                competitionId,
                "display label",
                progressAssessed,
                progressTotal,
                pendingAssessments,
                submitDeadline.toLocalDate(),
                0,
                0,
                false,
                batchIndex);

        assertEquals(true, viewModel.hasApplicationsToAssess());
        assertEquals(false, viewModel.hasPendingAssessments());
        assertEquals(batchIndex, viewModel.getBatchIndex());
    }

    @Test
    public void testHasAssessmentsToReviewAndApplicationsWaitingAcceptance() {

        competitionId = 1L;
        progressAssessed = 1L;
        progressTotal = 0L;
        pendingAssessments = 1L;
        batchIndex = 1L;

        ZonedDateTime submitDeadline = ZonedDateTime.now();
        AssessorDashboardActiveCompetitionViewModel viewModel = new AssessorDashboardActiveCompetitionViewModel(
                competitionId,
                "display label",
                progressAssessed,
                progressTotal,
                pendingAssessments,
                submitDeadline.toLocalDate(),
                0,
                0,
                false,
                batchIndex);

        assertEquals(true, viewModel.hasApplicationsToAssess());
        assertEquals(true, viewModel.hasPendingAssessments());
        assertEquals(batchIndex, viewModel.getBatchIndex());
    }
}