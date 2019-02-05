package org.innovateuk.ifs.assessment.review.viewmodel;

import org.innovateuk.ifs.application.common.viewmodel.SummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

public class AssessmentReviewApplicationSummaryViewModel {

    private final SummaryViewModel summaryViewModel;

    private final CompetitionResource currentCompetition;

    private final AssessmentReviewFeedbackViewModel feedbackViewModel;

    private final boolean collaborativeProject;

    public AssessmentReviewApplicationSummaryViewModel(SummaryViewModel summaryViewModel, CompetitionResource currentCompetition, AssessmentReviewFeedbackViewModel feedbackViewModel) {
        this.summaryViewModel = summaryViewModel;
        this.currentCompetition = currentCompetition;
        this.feedbackViewModel = feedbackViewModel;
        this.collaborativeProject = summaryViewModel.getCurrentApplication().isCollaborativeProject();
    }

    public SummaryViewModel getSummaryViewModel() {
        return summaryViewModel;
    }

    public CompetitionResource getCurrentCompetition() {
        return currentCompetition;
    }

    public AssessmentReviewFeedbackViewModel getFeedbackViewModel() {
        return feedbackViewModel;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }
}
