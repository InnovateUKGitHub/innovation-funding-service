package org.innovateuk.ifs.application.review.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

public class ReviewAndSubmitViewModel {

    private final ApplicationReadOnlyViewModel applicationReadOnlyViewModel;
    private final ApplicationResource application;
    private final CompetitionResource competition;
    private final boolean applicationReadyForSubmit;
    private final boolean userIsLeadApplicant;

    public ReviewAndSubmitViewModel(ApplicationReadOnlyViewModel applicationReadOnlyViewModel, ApplicationResource application, CompetitionResource competition, boolean applicationReadyForSubmit, boolean userIsLeadApplicant) {
        this.applicationReadOnlyViewModel = applicationReadOnlyViewModel;
        this.application = application;
        this.competition = competition;
        this.applicationReadyForSubmit = applicationReadyForSubmit;
        this.userIsLeadApplicant = userIsLeadApplicant;
    }


    public ApplicationReadOnlyViewModel getApplicationReadOnlyViewModel() {
        return applicationReadOnlyViewModel;
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
