package org.innovateuk.ifs.applicant.resource.dashboard;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.ApplicationState;

/**
 * Resource representing an application for use in the applicant dashboard.
 */
public class DashboardApplicationForEuGrantTransferResource extends DashboardApplicationResource {

    private int applicationProgress;
    private ApplicationState applicationState;
    private Long projectId;

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
        return new EqualsBuilder()
                .append(applicationProgress, that.applicationProgress)
                .append(applicationState, that.applicationState)
                .append(projectId, that.projectId)
                .append(title, that.title)
                .append(applicationId, that.applicationId)
                .append(competitionTitle, that.competitionTitle)
                .append(dashboardSection, that.dashboardSection)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationProgress)
                .append(applicationState)
                .append(projectId)
                .append(title)
                .append(applicationId)
                .append(competitionTitle)
                .append(dashboardSection)
                .toHashCode();
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