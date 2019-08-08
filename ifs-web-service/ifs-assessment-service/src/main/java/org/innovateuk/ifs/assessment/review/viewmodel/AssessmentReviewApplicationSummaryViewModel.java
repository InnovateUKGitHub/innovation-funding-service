package org.innovateuk.ifs.assessment.review.viewmodel;

import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.List;

public class AssessmentReviewApplicationSummaryViewModel {

    private final long applicationId;
    private final String applicationName;

    private final ApplicationReadOnlyViewModel applicationReadOnlyViewModel;

    private final CompetitionResource currentCompetition;

    private final List<AssessmentResource> feedbackSummary;

    public AssessmentReviewApplicationSummaryViewModel(long applicationId, String applicationName, ApplicationReadOnlyViewModel applicationReadOnlyViewModel, CompetitionResource currentCompetition, List<AssessmentResource> feedbackSummary) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.applicationReadOnlyViewModel = applicationReadOnlyViewModel;
        this.currentCompetition = currentCompetition;
        this.feedbackSummary = feedbackSummary;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public ApplicationReadOnlyViewModel getApplicationReadOnlyViewModel() {
        return applicationReadOnlyViewModel;
    }

    public CompetitionResource getCurrentCompetition() {
        return currentCompetition;
    }

    public List<AssessmentResource> getFeedbackSummary() {
        return feedbackSummary;
    }
}
