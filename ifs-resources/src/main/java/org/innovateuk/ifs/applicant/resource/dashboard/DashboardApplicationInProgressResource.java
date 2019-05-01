package org.innovateuk.ifs.applicant.resource.dashboard;

import com.google.common.base.Objects;
import org.innovateuk.ifs.application.resource.ApplicationState;

import java.time.ZonedDateTime;

/**
 * Resource representing an application for use in the applicant dashboard.
 */
public class DashboardApplicationInProgressResource extends DashboardApplicationResource {

    private boolean assignedToMe;
    private ApplicationState applicationState;
    private boolean leadApplicant;
    private ZonedDateTime endDate;
    private long daysLeft;
    private int applicationProgress;
    private boolean assignedToInterview;

    // Private constructor to enforce immutability
    private DashboardApplicationInProgressResource() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashboardApplicationInProgressResource that = (DashboardApplicationInProgressResource) o;
        return
                Objects.equal(title, that.title) &&
                applicationId == that.applicationId &&
                Objects.equal(competitionTitle, that.competitionTitle) &&
                dashboardSection == that.dashboardSection &&
                assignedToMe == that.assignedToMe &&
                Objects.equal(applicationState, that.applicationState) &&
                leadApplicant == that.leadApplicant &&
                Objects.equal(endDate, that.endDate) &&
                daysLeft == that.daysLeft &&
                applicationProgress == that.applicationProgress &&
                assignedToInterview == that.assignedToInterview;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(title, applicationId, competitionTitle, dashboardSection, assignedToMe, applicationState, leadApplicant, endDate, daysLeft, applicationProgress, assignedToInterview);
    }

    public static class DashboardApplicationInProgressResourceBuilder {

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

        public DashboardApplicationInProgressResourceBuilder withDashboardSection(DashboardSection dashboardSection) {
            this.dashboardSection = dashboardSection;
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

        public DashboardApplicationInProgressResource build(){
            DashboardApplicationInProgressResource result = new DashboardApplicationInProgressResource();
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

            return result;
        }
    }

}