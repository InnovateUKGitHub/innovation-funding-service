package org.innovateuk.ifs.review.model;


import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.service.AssessmentPanelRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionKeyStatisticsRestService;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;
import org.innovateuk.ifs.review.viewmodel.ReviewViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Assessment Panel dashboard
 */
@Component
public class ReviewModelPopulator {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionKeyStatisticsRestService competitionKeyStatisticsRestService;

    @Autowired
    private AssessmentPanelRestService assessmentPanelRestService;

    public ReviewViewModel populateModel(long competitionId) {
        CompetitionResource competition = competitionService.getById(competitionId);
        ReviewKeyStatisticsResource keyStatistics = competitionKeyStatisticsRestService
                .getAssessmentPanelKeyStatisticsByCompetition(competitionId)
                .getSuccess();

        boolean pendingReviewNotifications = assessmentPanelRestService
                .isPendingReviewNotifications(competitionId)
                .getSuccess();

        return new ReviewViewModel(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus(),
                keyStatistics.getApplicationsInPanel(),
                keyStatistics.getAssessorsPending(),
                keyStatistics.getAssessorsAccepted(),
                pendingReviewNotifications);
    }
}