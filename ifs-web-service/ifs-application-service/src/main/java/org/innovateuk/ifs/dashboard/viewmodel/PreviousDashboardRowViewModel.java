package org.innovateuk.ifs.dashboard.viewmodel;

import org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;

import java.time.LocalDate;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * View model for each application row in the 'Previous' section of the applicant dashboard.
 */
public class PreviousDashboardRowViewModel extends AbstractApplicantDashboardRowViewModel<PreviousDashboardRowViewModel> {

    private final ApplicationState applicationState;
    private final LocalDate startDate;

    public PreviousDashboardRowViewModel(String title,
                                         long applicationId,
                                         String competitionTitle,
                                         ApplicationState applicationState,
                                         LocalDate startDate) {
        super(title, applicationId, competitionTitle);
        this.applicationState = applicationState;
        this.startDate = startDate;
    }

    public PreviousDashboardRowViewModel(DashboardPreviousApplicationResource resource){
        super(resource.getTitle(), resource.getApplicationId(), resource.getCompetitionTitle());
        this.applicationState = resource.getApplicationState();
        this.startDate = resource.getStartDate();
    }

    public ApplicationState getApplicationState() {
        return applicationState;
    }

    public LocalDate getStartDate() {
        return startDate;
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

}
