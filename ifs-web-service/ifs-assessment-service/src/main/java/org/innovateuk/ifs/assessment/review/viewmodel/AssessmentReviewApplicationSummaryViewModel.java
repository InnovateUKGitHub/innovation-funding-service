package org.innovateuk.ifs.assessment.review.viewmodel;

import org.innovateuk.ifs.application.common.viewmodel.SummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

public class AssessmentReviewApplicationSummaryViewModel {

    private final SummaryViewModel summaryViewModel;

    private final CompetitionResource competition;

    public AssessmentReviewApplicationSummaryViewModel(SummaryViewModel summaryViewModel, CompetitionResource competition) {
        this.summaryViewModel = summaryViewModel;
        this.competition = competition;
    }

    public SummaryViewModel getSummaryViewModel() {
        return summaryViewModel;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }
}
