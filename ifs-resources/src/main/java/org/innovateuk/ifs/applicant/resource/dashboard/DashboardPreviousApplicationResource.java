package org.innovateuk.ifs.applicant.resource.dashboard;

import com.google.common.base.Objects;
import org.innovateuk.ifs.application.resource.ApplicationState;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashboardPreviousApplicationResource that = (DashboardPreviousApplicationResource) o;
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

            return result;
        }
    }

}