package org.innovateuk.ifs.application.viewmodel.overview;

import org.innovateuk.ifs.user.resource.UserResource;

/**
 * View model for the application overview - users
 */
public class ApplicationOverviewUserViewModel {
    private Boolean userIsLeadApplicant;
    private UserResource leadApplicant;
    private Boolean ableToSubmitApplication;

    public ApplicationOverviewUserViewModel(Boolean userIsLeadApplicant, UserResource leadApplicant, Boolean ableToSubmitApplication) {
        this.userIsLeadApplicant = userIsLeadApplicant;
        this.leadApplicant = leadApplicant;
        this.ableToSubmitApplication = ableToSubmitApplication;
    }

    public Boolean getUserIsLeadApplicant() {
        return userIsLeadApplicant;
    }

    public UserResource getLeadApplicant() {
        return leadApplicant;
    }

    public Boolean getAbleToSubmitApplication() {
        return ableToSubmitApplication;
    }
}
