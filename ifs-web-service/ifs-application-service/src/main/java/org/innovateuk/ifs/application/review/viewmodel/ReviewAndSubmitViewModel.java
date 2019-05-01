package org.innovateuk.ifs.application.review.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.summary.viewmodel.NewApplicationSummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

public class ReviewAndSubmitViewModel {

    private final NewApplicationSummaryViewModel applicationSummaryViewModel;
    private final ApplicationResource application;
    private final CompetitionResource competition;

    public ReviewAndSubmitViewModel(NewApplicationSummaryViewModel applicationSummaryViewModel, long applicationId) {
        this.applicationSummaryViewModel = applicationSummaryViewModel;
        this.applicationId = applicationId;
    }

    public NewApplicationSummaryViewModel getApplicationSummaryViewModel() {
        return applicationSummaryViewModel;
    }

    public long getApplicationId() {
        return applicationId;
    }
}
