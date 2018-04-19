package org.innovateuk.ifs.dashboard.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationState;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * View model for each application row in the 'Previous' section of the applicant dashboard.
 */
public class PreviousDashboardRowViewModel extends AbstractApplicantDashboardRowViewModel<PreviousDashboardRowViewModel> {

    private final ApplicationState applicationState;

    private final boolean withdrawn;

    public PreviousDashboardRowViewModel(String title,
                                         long applicationId,
                                         String competitionTitle,
                                         ApplicationState applicationState,
                                         boolean withdrawn) {
        super(title, applicationId, competitionTitle);
        this.applicationState = applicationState;
        this.withdrawn = withdrawn;
    }

    public boolean isWithdrawn() {
        return withdrawn;
    }

    /* View logic */
    public boolean isRejected() {
        return ApplicationState.REJECTED.equals(applicationState);
    }

    public boolean isApproved() {
        return ApplicationState.APPROVED.equals(applicationState);
    }

    public boolean isCreatedOrOpen() {
        return ApplicationState.OPEN.equals(applicationState)
                ||  ApplicationState.CREATED.equals(applicationState);
    }



    public boolean isInformedIneligible() {
        return ApplicationState.INELIGIBLE_INFORMED.equals(applicationState);
    }

    @Override
    public String getLinkUrl() {
        return String.format("/application/%s/summary", getApplicationNumber());
    }

    @Override
    public String getTitle() {
        return !isNullOrEmpty(title) ? title : "Untitled application";
    }

    @Override
    public int compareTo(PreviousDashboardRowViewModel o) {
        return Long.compare(getApplicationNumber(), o.getApplicationNumber());
    }
}
