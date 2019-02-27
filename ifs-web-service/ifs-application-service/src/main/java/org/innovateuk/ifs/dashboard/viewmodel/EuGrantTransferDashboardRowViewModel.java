package org.innovateuk.ifs.dashboard.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationState;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;
import static org.innovateuk.ifs.application.resource.ApplicationState.*;

/**
 * View model for each application row in the 'Grant transfer' section of the applicant dashboard.
 */
public class EuGrantTransferDashboardRowViewModel extends
        AbstractApplicantDashboardRowViewModel<EuGrantTransferDashboardRowViewModel> {
    private final ApplicationState applicationState;
    private final int applicationProgress;
    private final Long projectId;

    public EuGrantTransferDashboardRowViewModel(String title,
                                                long applicationId,
                                                String competitionTitle,
                                                ApplicationState applicationState,
                                                int applicationProgress,
                                                Long projectId) {
        super(title, applicationId, competitionTitle);
        this.applicationState = applicationState;
        this.applicationProgress = applicationProgress;
        this.projectId = projectId;
    }

    public int getApplicationProgress() {
        return applicationProgress;
    }

    /* view logic */
    public boolean isSubmitted() {
        return SUBMITTED.equals(applicationState) ||
                INELIGIBLE.equals(applicationState);
    }

    public boolean isSuccessful() {
        return APPROVED.equals(applicationState);
    }

    public boolean isInProgress() {
        return CREATED.equals(applicationState) ||
                OPEN.equals(applicationState);
    }

    public boolean isIneligible() {
        return INELIGIBLE_INFORMED.equals(applicationState);
    }

    @Override
    public String getLinkUrl() {
        if (isSuccessful()) {
            return String.format("/project-setup/project/%s", projectId);
        } else if (isSubmitted()) {
            return format("/application/%s/track", getApplicationNumber());
        } else {
            return format("/application/%s", getApplicationNumber());
        }
    }

    @Override
    public String getTitle() {
        if(!isNullOrEmpty(title)) {
            return title;
        }

        if(isSubmitted()) {
            return "Untitled application";
        }

        return "Untitled application (start here)";
    }

    @Override
    public int compareTo(EuGrantTransferDashboardRowViewModel o) {
        if (isSuccessful() != o.isSuccessful()) {
            return isSuccessful() ? -1 : 1;
        }
        return Long.compare(getApplicationNumber(), o.getApplicationNumber());
    }
}
