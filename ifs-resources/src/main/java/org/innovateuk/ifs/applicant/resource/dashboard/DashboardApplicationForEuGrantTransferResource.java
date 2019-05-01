package org.innovateuk.ifs.applicant.resource.dashboard;

import com.google.common.base.Objects;
import org.innovateuk.ifs.application.resource.ApplicationState;

/**
 * Resource representing an application for use in the applicant dashboard.
 */
public class DashboardApplicationForEuGrantTransferResource extends DashboardApplicationResource {

    private int applicationProgress;
    private ApplicationState applicationState;
    private Long projectId;

    // Private constructor to enforce immutability
    private DashboardApplicationForEuGrantTransferResource() {
    }

    public ApplicationState getApplicationState() {
        return applicationState;
    }

    public int getApplicationProgress() {
        return applicationProgress;
    }

    public Long getProjectId() {
        return projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashboardApplicationForEuGrantTransferResource that = (DashboardApplicationForEuGrantTransferResource) o;
        return
                Objects.equal(title, that.title) &&
                applicationId == that.applicationId &&
                Objects.equal(competitionTitle, that.competitionTitle) &&
                dashboardSection == that.dashboardSection &&
                applicationProgress == that.applicationProgress &&
                Objects.equal(applicationState, that.applicationState) &&
                Objects.equal(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(title, applicationId, competitionTitle, dashboardSection, applicationProgress, applicationState, projectId);
    }

    public static class DashboardApplicationForEuGrantTransferResourceBuilder {

        private String title;
        private long applicationId;
        private String competitionTitle;
        private DashboardSection dashboardSection;
        private int applicationProgress;
        private ApplicationState applicationState;
        private Long projectId;

        public DashboardApplicationForEuGrantTransferResourceBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public DashboardApplicationForEuGrantTransferResourceBuilder withApplicationId(long applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public DashboardApplicationForEuGrantTransferResourceBuilder withCompetitionTitle(String competitionTitle) {
            this.competitionTitle = competitionTitle;
            return this;
        }

        public DashboardApplicationForEuGrantTransferResourceBuilder withDashboardSection(DashboardSection dashboardSection) {
            this.dashboardSection = dashboardSection;
            return this;
        }

        public DashboardApplicationForEuGrantTransferResourceBuilder withApplicationProgress(int applicationProgress) {
            this.applicationProgress = applicationProgress;
            return this;
        }

        public DashboardApplicationForEuGrantTransferResourceBuilder withApplicationState(ApplicationState applicationState) {
            this.applicationState = applicationState;
            return this;
        }

        public DashboardApplicationForEuGrantTransferResourceBuilder withProjectId(Long projectId) {
            this.projectId = projectId;
            return this;
        }

        public DashboardApplicationForEuGrantTransferResource build(){
            DashboardApplicationForEuGrantTransferResource result = new DashboardApplicationForEuGrantTransferResource();
            result.title = this.title;
            result.applicationId = this.applicationId;
            result.competitionTitle = this.competitionTitle;
            result.dashboardSection = this.dashboardSection;
            result.applicationProgress = this.applicationProgress;
            result.applicationState = this.applicationState;
            result.projectId = this.projectId;

            return result;
        }
    }

}