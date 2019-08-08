package org.innovateuk.ifs.assessment.review.viewmodel;

import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

public class AssessmentReviewApplicationSummaryViewModel {

    private final ApplicationReadOnlyViewModel ApplicationReadOnlyViewModel;

    private final CompetitionResource currentCompetition;

    private final AssessmentReviewFeedbackViewModel feedbackViewModel;

    private final long applicationId;

    public AssessmentReviewApplicationSummaryViewModel(ApplicationReadOnlyViewModel ApplicationReadOnlyViewModel,
                                                       CompetitionResource currentCompetition,
                                                       AssessmentReviewFeedbackViewModel feedbackViewModel,
                                                       long applicationId) {
        this.ApplicationReadOnlyViewModel = ApplicationReadOnlyViewModel;
        this.currentCompetition = currentCompetition;
        this.feedbackViewModel = feedbackViewModel;
        this.applicationId = applicationId;
    }

    public ApplicationReadOnlyViewModel getApplicationReadOnlyViewModel() {
        return ApplicationReadOnlyViewModel;
    }

    public CompetitionResource getCurrentCompetition() {
        return currentCompetition;
    }

    public AssessmentReviewFeedbackViewModel getFeedbackViewModel() {
        return feedbackViewModel;
    }

    public long getApplicationId() {
        return applicationId;
    }
}
