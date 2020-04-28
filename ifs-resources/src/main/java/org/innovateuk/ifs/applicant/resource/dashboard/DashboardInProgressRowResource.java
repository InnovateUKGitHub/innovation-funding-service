package org.innovateuk.ifs.applicant.resource.dashboard;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.ApplicationState;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.IN_PROGRESS;

/**
 * Resource representing an application for use in the in progress section of the applicant dashboard.
 */
public class DashboardInProgressRowResource extends DashboardRowResource {

    private boolean assignedToMe;
    private ApplicationState applicationState;
    private boolean leadApplicant;
    private ZonedDateTime endDate;
    private long daysLeft;
    private int applicationProgress;
    private boolean assignedToInterview;
    private LocalDate startDate;
    private boolean showReopenLink;

    // Private constructor to enforce immutability
    private DashboardInProgressRowResource() {
        this.dashboardSection = IN_PROGRESS;
    }

    public boolean isAssignedToMe() {
        return assignedToMe;
    }

    public ApplicationState getApplicationState() {
        return applicationState;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public boolean isShowReopenLink() {
        return showReopenLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashboardInProgressRowResource that = (DashboardInProgressRowResource) o;
        return new EqualsBuilder()
                .append(assignedToMe, that.assignedToMe)
                .append(leadApplicant, that.leadApplicant)
                .append(daysLeft, that.daysLeft)
                .append(applicationProgress, that.applicationProgress)
                .append(assignedToInterview, that.assignedToInterview)
                .append(applicationState, that.applicationState)
                .append(endDate, that.endDate)
                .append(title, that.title)
                .append(applicationId, that.applicationId)
                .append(competitionTitle, that.competitionTitle)
                .append(dashboardSection, that.dashboardSection)
                .append(startDate, that.startDate)
                .append(showReopenLink, that.showReopenLink)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assignedToMe)
                .append(applicationState)
                .append(leadApplicant)
                .append(endDate)
                .append(daysLeft)
                .append(applicationProgress)
                .append(assignedToInterview)
                .append(title)
                .append(applicationId)
                .append(competitionTitle)
                .append(dashboardSection)
                .append(startDate)
                .append(showReopenLink)
                .toHashCode();
    }

    public static class DashboardApplicationInProgressResourceBuilder {

        private String title;
        private long applicationId;
        private String competitionTitle;
        private boolean assignedToMe;
        private ApplicationState applicationState;
        private boolean leadApplicant;
        private ZonedDateTime endDate;
        private long daysLeft;
        private int applicationProgress;
        private boolean assignedToInterview;
        private LocalDate startDate;
        private boolean showReopenLink;

        public DashboardApplicationInProgressResourceBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public DashboardApplicationInProgressResourceBuilder withApplicationId(long applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public DashboardApplicationInProgressResourceBuilder withCompetitionTitle(String competitionTitle) {
            this.competitionTitle = competitionTitle;
            return this;
        }

        public DashboardApplicationInProgressResourceBuilder withAssignedToMe(boolean assignedToMe) {
            this.assignedToMe = assignedToMe;
            return this;
        }

        public DashboardApplicationInProgressResourceBuilder withApplicationState(ApplicationState applicationState) {
            this.applicationState = applicationState;
            return this;
        }

        public DashboardApplicationInProgressResourceBuilder withLeadApplicant(boolean leadApplicant) {
            this.leadApplicant = leadApplicant;
            return this;
        }

        public DashboardApplicationInProgressResourceBuilder withEndDate(ZonedDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public DashboardApplicationInProgressResourceBuilder withDaysLeft(long daysLeft) {
            this.daysLeft = daysLeft;
            return this;
        }

        public DashboardApplicationInProgressResourceBuilder withApplicationProgress(int applicationProgress) {
            this.applicationProgress = applicationProgress;
            return this;
        }

        public DashboardApplicationInProgressResourceBuilder withAssignedToInterview(boolean assignedToInterview) {
            this.assignedToInterview = assignedToInterview;
            return this;
        }

        public DashboardApplicationInProgressResourceBuilder withStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public DashboardApplicationInProgressResourceBuilder withShowReopenLink(boolean showReopenLink) {
            this.showReopenLink = showReopenLink;
            return this;
        }

        public DashboardInProgressRowResource build(){
            DashboardInProgressRowResource result = new DashboardInProgressRowResource();
            result.title = this.title;
            result.applicationId = this.applicationId;
            result.competitionTitle = this.competitionTitle;
            result.assignedToMe = this.assignedToMe;
            result.applicationState = this.applicationState;
            result.leadApplicant = this.leadApplicant;
            result.endDate = this.endDate;
            result.daysLeft = this.daysLeft;
            result.applicationProgress = this.applicationProgress;
            result.assignedToInterview = this.assignedToInterview;
            result.startDate = this.startDate;
            result.showReopenLink = this.showReopenLink;

            return result;
        }
    }

}