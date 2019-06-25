package org.innovateuk.ifs.applicant.resource.dashboard;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.ApplicationState;

import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * Resource representing an application for use in the applicant dashboard.
 */
public class DashboardPreviousApplicationResource extends DashboardApplicationResource {

    private boolean assignedToMe;
    private ApplicationState applicationState;
    private boolean leadApplicant;
    private ZonedDateTime endDate;
    private long daysLeft;
    private int applicationProgress;
    private boolean assignedToInterview;
    private LocalDate startDate;

    // Private constructor to enforce immutability
    private DashboardPreviousApplicationResource() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashboardPreviousApplicationResource that = (DashboardPreviousApplicationResource) o;
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
                .toHashCode();
    }

    public static class DashboardPreviousApplicationResourceBuilder {

        private String title;
        private long applicationId;
        private String competitionTitle;
        private DashboardSection dashboardSection;
        private boolean assignedToMe;
        private ApplicationState applicationState;
        private boolean leadApplicant;
        private ZonedDateTime endDate;
        private long daysLeft;
        private int applicationProgress;
        private boolean assignedToInterview;
        private LocalDate startDate;

        public DashboardPreviousApplicationResourceBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public DashboardPreviousApplicationResourceBuilder withApplicationId(long applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public DashboardPreviousApplicationResourceBuilder withCompetitionTitle(String competitionTitle) {
            this.competitionTitle = competitionTitle;
            return this;
        }

        public DashboardPreviousApplicationResourceBuilder withDashboardSection(DashboardSection dashboardSection) {
            this.dashboardSection = dashboardSection;
            return this;
        }

        public DashboardPreviousApplicationResourceBuilder withAssignedToMe(boolean assignedToMe) {
            this.assignedToMe = assignedToMe;
            return this;
        }

        public DashboardPreviousApplicationResourceBuilder withApplicationState(ApplicationState applicationState) {
            this.applicationState = applicationState;
            return this;
        }

        public DashboardPreviousApplicationResourceBuilder withLeadApplicant(boolean leadApplicant) {
            this.leadApplicant = leadApplicant;
            return this;
        }

        public DashboardPreviousApplicationResourceBuilder withEndDate(ZonedDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public DashboardPreviousApplicationResourceBuilder withDaysLeft(long daysLeft) {
            this.daysLeft = daysLeft;
            return this;
        }

        public DashboardPreviousApplicationResourceBuilder withApplicationProgress(int applicationProgress) {
            this.applicationProgress = applicationProgress;
            return this;
        }

        public DashboardPreviousApplicationResourceBuilder withAssignedToInterview(boolean assignedToInterview) {
            this.assignedToInterview = assignedToInterview;
            return this;
        }

        public DashboardPreviousApplicationResourceBuilder withStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public DashboardPreviousApplicationResource build(){
            DashboardPreviousApplicationResource result = new DashboardPreviousApplicationResource();
            result.title = this.title;
            result.applicationId = this.applicationId;
            result.competitionTitle = this.competitionTitle;
            result.dashboardSection = this.dashboardSection;
            result.assignedToMe = this.assignedToMe;
            result.applicationState = this.applicationState;
            result.leadApplicant = this.leadApplicant;
            result.endDate = this.endDate;
            result.daysLeft = this.daysLeft;
            result.applicationProgress = this.applicationProgress;
            result.assignedToInterview = this.assignedToInterview;
            result.startDate = startDate;

            return result;
        }
    }

}