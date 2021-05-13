package org.innovateuk.ifs.dashboard.viewmodel;

import org.innovateuk.ifs.applicant.resource.dashboard.DashboardInProgressRowResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.util.TimeZoneUtil;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;
import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;

/**
 * View model for each application row in the 'In progress' section of the applicant dashboard.
 */
public class InProgressDashboardRowViewModel extends AbstractApplicantDashboardRowViewModel {

    private static final int ONE_HUNDRED_PERCENT = 100;

    private final boolean assignedToMe;
    private final ApplicationState applicationState;
    private final boolean leadApplicant;
    private final ZonedDateTime endDate;
    private final Long daysLeft;
    private final int applicationProgress;
    private final boolean assignedToInterview;
    private final LocalDate startDate;
    private final boolean showReopenLink;
    private final boolean hasAssessmentStage;
    private final boolean alwaysOpen;

    public InProgressDashboardRowViewModel(String title,
                                           long applicationId,
                                           String competitionTitle,
                                           boolean assignedToMe,
                                           ApplicationState applicationState,
                                           boolean leadApplicant,
                                           ZonedDateTime endDate,
                                           Long daysLeft,
                                           int applicationProgress,
                                           boolean assignedToInterview,
                                           LocalDate startDate,
                                           boolean showReopenLink,
                                           boolean hasAssessmentStage,
                                           boolean alwaysOpen) {
        super(title, applicationId, competitionTitle);
        this.assignedToMe = assignedToMe;
        this.applicationState = applicationState;
        this.leadApplicant = leadApplicant;
        this.endDate = endDate;
        this.daysLeft = daysLeft;
        this.applicationProgress = applicationProgress;
        this.assignedToInterview = assignedToInterview;
        this.startDate = startDate;
        this.showReopenLink = showReopenLink;
        this.hasAssessmentStage = hasAssessmentStage;
        this.alwaysOpen = alwaysOpen;
    }

    public InProgressDashboardRowViewModel (DashboardInProgressRowResource resource){
        super(resource.getTitle(), resource.getApplicationId(), resource.getCompetitionTitle());
        this.assignedToMe = resource.isAssignedToMe();
        this.applicationState = resource.getApplicationState();
        this.leadApplicant = resource.isLeadApplicant();
        this.endDate = resource.getEndDate();
        this.daysLeft = resource.getDaysLeft();
        this.applicationProgress = resource.getApplicationProgress();
        this.assignedToInterview = resource.isAssignedToInterview();
        this.startDate = resource.getStartDate();
        this.showReopenLink = resource.isShowReopenLink();
        this.hasAssessmentStage = resource.isHasAssessmentStage();
        this.alwaysOpen = resource.isAlwaysOpen();
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

    public Long getDaysLeft() {
        return daysLeft;
    }

    public int getApplicationProgress() {
        return applicationProgress;
    }

    public boolean isAssignedToInterview() {
        return assignedToInterview;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public boolean isHasAssessmentStage() {
        return hasAssessmentStage;
    }

    public boolean isShowReopenLink() {
        return showReopenLink;
    }

    public boolean isAlwaysOpen() {
        return alwaysOpen;
    }

    /* view logic */

    public boolean isShouldDisplayEndDate() {
        return hasAssessmentStage && daysLeft != null;
    }

    public boolean isSubmitted() {
        return SUBMITTED.equals(applicationState) ||
                INELIGIBLE.equals(applicationState);
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

    public boolean isApplicationComplete() {
        return applicationProgress == ONE_HUNDRED_PERCENT;
    }

    public String getProgressMessage() {
        return isApplicationComplete() ? "Ready to review and submit" : applicationProgress + "% complete";
    }

    @Override
    public String getLinkUrl() {
        if (isSubmitted()) {
            if (assignedToInterview) {
                return format("/application/%s/summary", getApplicationNumber());
            } else {
                return format("/application/%s/track", getApplicationNumber());
            }
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

}
