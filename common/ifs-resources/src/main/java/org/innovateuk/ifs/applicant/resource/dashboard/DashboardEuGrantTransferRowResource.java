package org.innovateuk.ifs.applicant.resource.dashboard;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.ApplicationState;

import java.time.LocalDate;

import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.EU_GRANT_TRANSFER;

/**
 * Resource representing an application or project for use in the EU grant transfer section of the applicant dashboard.
 */
public class DashboardEuGrantTransferRowResource extends DashboardRowResource {

    private int applicationProgress;
    private ApplicationState applicationState;
    private Long projectId;
    private LocalDate startDate;

    private DashboardEuGrantTransferRowResource() {
        this.dashboardSection = EU_GRANT_TRANSFER;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashboardEuGrantTransferRowResource that = (DashboardEuGrantTransferRowResource) o;
        return new EqualsBuilder()
                .append(applicationProgress, that.applicationProgress)
                .append(applicationState, that.applicationState)
                .append(projectId, that.projectId)
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
                .append(applicationProgress)
                .append(applicationState)
                .append(projectId)
                .append(title)
                .append(applicationId)
                .append(competitionTitle)
                .append(dashboardSection)
                .append(startDate)
                .toHashCode();
    }

    public static class DashboardApplicationForEuGrantTransferResourceBuilder {

        private String title;
        private long applicationId;
        private String competitionTitle;
        private int applicationProgress;
        private ApplicationState applicationState;
        private Long projectId;
        private LocalDate startDate;

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

        public DashboardApplicationForEuGrantTransferResourceBuilder withStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public DashboardEuGrantTransferRowResource build(){
            DashboardEuGrantTransferRowResource result = new DashboardEuGrantTransferRowResource();
            result.title = this.title;
            result.applicationId = this.applicationId;
            result.competitionTitle = this.competitionTitle;
            result.applicationProgress = this.applicationProgress;
            result.applicationState = this.applicationState;
            result.projectId = this.projectId;
            result.startDate = this.startDate;

            return result;
        }
    }

}