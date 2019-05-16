package org.innovateuk.ifs.applicant.resource.dashboard;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Resource representing an application for use in the applicant dashboard.
 */
public class DashboardApplicationInSetupResource extends DashboardApplicationResource {

    private long projectId;
    private String projectTitle;

    // Private constructor to enforce immutability
    private DashboardApplicationInSetupResource() {
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashboardApplicationInSetupResource that = (DashboardApplicationInSetupResource) o;
        return new EqualsBuilder()
                .append(projectId, that.projectId)
                .append(projectTitle, that.projectTitle)
                .append(title, that.title)
                .append(applicationId, that.applicationId)
                .append(competitionTitle, that.competitionTitle)
                .append(dashboardSection, that.dashboardSection)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectId)
                .append(projectTitle)
                .append(title)
                .append(applicationId)
                .append(competitionTitle)
                .append(dashboardSection)
                .toHashCode();
    }

    public static class DashboardApplicationInSetupResourceBuilder {

        private String title;
        private long applicationId;
        private String competitionTitle;
        private long projectId;
        private String projectTitle;
        private DashboardSection dashboardSection;

        public DashboardApplicationInSetupResourceBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public DashboardApplicationInSetupResourceBuilder withApplicationId(long applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public DashboardApplicationInSetupResourceBuilder withCompetitionTitle(String competitionTitle) {
            this.competitionTitle = competitionTitle;
            return this;
        }

        public DashboardApplicationInSetupResourceBuilder withProjectId(long projectId) {
            this.projectId = projectId;
            return this;
        }

        public DashboardApplicationInSetupResourceBuilder withProjectTitle(String projectTitle) {
            this.projectTitle = projectTitle;
            return this;
        }

        public DashboardApplicationInSetupResourceBuilder withDashboardSection(DashboardSection dashboardSection) {
            this.dashboardSection = dashboardSection;
            return this;
        }

        public DashboardApplicationInSetupResource build(){
            DashboardApplicationInSetupResource result = new DashboardApplicationInSetupResource();
            result.applicationId = this.applicationId;
            result.competitionTitle = this.competitionTitle;
            result.projectId = this.projectId;
            result.projectTitle = this.projectTitle;
            result.title = this.title;
            result.dashboardSection = this.dashboardSection;

            return result;
        }
    }

}