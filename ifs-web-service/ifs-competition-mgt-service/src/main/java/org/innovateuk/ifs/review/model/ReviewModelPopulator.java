package org.innovateuk.ifs.review.model;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionKeyApplicationStatisticsRestService;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;
import org.innovateuk.ifs.review.service.ReviewRestService;
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
    private CompetitionKeyApplicationStatisticsRestService competitionKeyApplicationStatisticsRestService;

    @Autowired
    private ReviewRestService reviewRestService;

    public ReviewViewModel populateModel(long competitionId) {
        CompetitionResource competition = competitionService.getById(competitionId);
        ReviewKeyStatisticsResource keyStatistics = competitionKeyApplicationStatisticsRestService
                .getReviewKeyStatisticsByCompetition(competitionId)
                .getSuccess();

        boolean pendingReviewNotifications = reviewRestService
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