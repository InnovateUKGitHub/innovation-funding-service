package org.innovateuk.ifs.dashboard.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.util.TimeZoneUtil;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.innovateuk.ifs.application.resource.ApplicationState.*;

/**
 * View model for each application row in the 'In progress' section of the applicant dashboard.
 */
public class InProgressDashboardRowViewModel extends AbstractApplicantDashboardRowViewModel<InProgressDashboardRowViewModel> {
    private final boolean assignedToMe;
    private final ApplicationState applicationState;
    private final boolean leadApplicant;
    private final ZonedDateTime endDate;
    private final long daysLeft;
    private final int applicationProgress;
    private final boolean assignedToInterview;

    public InProgressDashboardRowViewModel(String title, long applicationId, String competitionTitle,
                                           boolean assignedToMe, ApplicationState applicationState,
                                           boolean leadApplicant, ZonedDateTime endDate, long daysLeft,
                                           int applicationProgress, boolean assignedToInterview) {
        super(title, applicationId, competitionTitle);
        this.assignedToMe = assignedToMe;
        this.applicationState = applicationState;
        this.leadApplicant = leadApplicant;
        this.endDate = endDate;
        this.daysLeft = daysLeft;
        this.applicationProgress = applicationProgress;
        this.assignedToInterview = assignedToInterview;
    }

    public boolean isAssignedToMe() {
        return assignedToMe;
    }

    public boolean isLeadApplicant() {
        return leadApplicant;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public int getApplicationProgress() {
        return applicationProgress;
    }

    public boolean isAssignedToInterview() {
        return assignedToInterview;
    }

    /* view logic */
    public boolean isSubmitted() {
        return SUBMITTED.equals(applicationState) ||
                INELIGIBLE.equals(applicationState);
    }

    public boolean isCreated() {
        return CREATED.equals(applicationState);
    }

    public boolean isWithin24Hours() {
        long hoursLeft = getHoursLeftBeforeSubmit();
        return hoursLeft >= 0 && hoursLeft < 24;
    }

    public long getHoursLeftBeforeSubmit() {
        return Duration.between(ZonedDateTime.now(), endDate).toHours();
    }

    public boolean isClosingToday() {
        LocalDate endDay = TimeZoneUtil.toUkTimeZone(endDate).toLocalDate();
        LocalDate today = TimeZoneUtil.toUkTimeZone(ZonedDateTime.now()).toLocalDate();

        return today.equals(endDay);
    }

    @Override
    public String getLinkUrl() {
        if (isSubmitted()) {
            if (assignedToInterview) {
                return String.format("/application/%s/summary", getApplicationNumber());
            } else {
                return String.format("/application/%s/track", getApplicationNumber());
            }
        } else if (isCreated() && leadApplicant) {
            return String.format("/application/%s/team", getApplicationNumber());
        } else {
            return String.format("/application/%s", getApplicationNumber());
        }
    }

    @Override
    public String getTitle() {
        return !isNullOrEmpty(title) ? title :
                (isSubmitted() ? "Untitled application" :  "Untitled application (start here)");
    }

    @Override
    public int compareTo(InProgressDashboardRowViewModel o) {
        if (assignedToInterview != o.isAssignedToInterview()) {
            return assignedToInterview ? -1 : 1;
        }
        return Long.compare(getApplicationNumber(), o.getApplicationNumber());
    }
}
