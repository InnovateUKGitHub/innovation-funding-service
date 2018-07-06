package org.innovateuk.ifs.assessment.review.viewmodel;

import org.innovateuk.ifs.application.common.viewmodel.SummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

public class AssessmentReviewApplicationSummaryViewModel {

    private final SummaryViewModel summaryViewModel;

    private final CompetitionResource competition;

    private final AssessmentReviewFeedbackViewModel feedbackViewModel;

    public AssessmentReviewApplicationSummaryViewModel(SummaryViewModel summaryViewModel, CompetitionResource competition, AssessmentReviewFeedbackViewModel feedbackViewModel) {
        this.summaryViewModel = summaryViewModel;
        this.competition = competition;
        this.feedbackViewModel = feedbackViewModel;
    }

    public SummaryViewModel getSummaryViewModel() {
        return summaryViewModel;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public AssessmentReviewFeedbackViewModel getFeedbackViewModel() {
        return feedbackViewModel;
    }
}
