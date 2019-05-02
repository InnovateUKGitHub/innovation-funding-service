package org.innovateuk.ifs.application.review.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationRowsSummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

public class ReviewAndSubmitViewModel {

    private final ApplicationRowsSummaryViewModel applicationSummaryViewModel;
    private final ApplicationResource application;
    private final CompetitionResource competition;
    private final boolean applicationReadyForSubmit;
    private final boolean userIsLeadApplicant;

    public ReviewAndSubmitViewModel(ApplicationRowsSummaryViewModel applicationSummaryViewModel, ApplicationResource application, CompetitionResource competition, boolean applicationReadyForSubmit, boolean userIsLeadApplicant) {
        this.applicationSummaryViewModel = applicationSummaryViewModel;
        this.application = application;
        this.competition = competition;
        this.applicationReadyForSubmit = applicationReadyForSubmit;
        this.userIsLeadApplicant = userIsLeadApplicant;
    }

    public ApplicationRowsSummaryViewModel getApplicationSummaryViewModel() {
        return applicationSummaryViewModel;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public boolean isApplicationReadyForSubmit() {
        return applicationReadyForSubmit;
    }

    public boolean isUserIsLeadApplicant() {
        return userIsLeadApplicant;
    }
}
