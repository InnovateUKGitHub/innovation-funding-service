package org.innovateuk.ifs.dashboard.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationState;

import java.time.LocalDate;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;
import static org.innovateuk.ifs.application.resource.ApplicationState.APPROVED;
import static org.innovateuk.ifs.application.resource.ApplicationState.CREATED;
import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE;
import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE_INFORMED;
import static org.innovateuk.ifs.application.resource.ApplicationState.OPEN;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;

/**
 * View model for each application row in the 'Grant transfer' section of the applicant dashboard.
 */
public class EuGrantTransferDashboardRowViewModel extends
        AbstractApplicantDashboardRowViewModel<EuGrantTransferDashboardRowViewModel>
        implements Comparable<EuGrantTransferDashboardRowViewModel> {
    private final ApplicationState applicationState;
    private final int applicationProgress;
    private final Long projectId;
    private final LocalDate startDate;

    public EuGrantTransferDashboardRowViewModel(String title,
                                                long applicationId,
                                                String competitionTitle,
                                                ApplicationState applicationState,
                                                int applicationProgress,
                                                Long projectId,
                                                LocalDate startDate) {
        super(title, applicationId, competitionTitle);
        this.applicationState = applicationState;
        this.applicationProgress = applicationProgress;
        this.projectId = projectId;
        this.startDate = startDate;
    }

    public int getApplicationProgress() {
        return applicationProgress;
    }

    public LocalDate getStartDate() {
        return startDate;
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
