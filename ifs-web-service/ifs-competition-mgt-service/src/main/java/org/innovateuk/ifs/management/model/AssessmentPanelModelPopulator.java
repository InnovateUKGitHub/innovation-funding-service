package org.innovateuk.ifs.management.model;


import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.review.resource.AssessmentPanelKeyStatisticsResource;
import org.innovateuk.ifs.assessment.service.AssessmentPanelRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionKeyStatisticsRestService;
import org.innovateuk.ifs.management.viewmodel.ReviewPanelDashboardViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Assessment Panel dashboard
 */
@Component
public class AssessmentPanelModelPopulator {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionKeyStatisticsRestService competitionKeyStatisticsRestService;

    @Autowired
    private AssessmentPanelRestService assessmentPanelRestService;

    public ReviewPanelDashboardViewModel populateModel(long competitionId) {
        CompetitionResource competition = competitionService.getById(competitionId);
        AssessmentPanelKeyStatisticsResource keyStatistics = competitionKeyStatisticsRestService
                .getAssessmentPanelKeyStatisticsByCompetition(competitionId)
                .getSuccess();

        boolean pendingReviewNotifications = assessmentPanelRestService
                .isPendingReviewNotifications(competitionId)
                .getSuccess();

        return new ReviewPanelDashboardViewModel(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus(),
                keyStatistics.getApplicationsInPanel(),
                keyStatistics.getAssessorsPending(),
                keyStatistics.getAssessorsAccepted(),
                pendingReviewNotifications);
    }
}