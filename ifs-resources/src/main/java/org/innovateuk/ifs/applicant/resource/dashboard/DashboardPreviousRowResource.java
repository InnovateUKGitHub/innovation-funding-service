package org.innovateuk.ifs.applicant.resource.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.project.resource.ProjectState;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.PREVIOUS;

/**
 * Resource representing an application or project for use in the previous section of the applicant dashboard.
 */
public class DashboardPreviousRowResource extends DashboardRowResource {

    private boolean assignedToMe;
    private ApplicationState applicationState;
    private ProjectState projectState;
    private Long projectId;
    private boolean leadApplicant;
    private ZonedDateTime endDate;
    private long daysLeft;
    private int applicationProgress;
    private boolean assignedToInterview;
    private LocalDate startDate;
    private boolean collaborationLevelSingle;

    // Private constructor to enforce immutability
    private DashboardPreviousRowResource() {
        this.dashboardSection = PREVIOUS;
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

    public ProjectState getProjectState() {
        return projectState;
    }

    public Long getProjectId() {
        return projectId;
    }

    public boolean isCollaborationLevelSingle() {
        return collaborationLevelSingle;
    }

    @JsonIgnore
    public boolean activeProject() {
        return projectState != null && projectState.isActive();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashboardPreviousRowResource that = (DashboardPreviousRowResource) o;
        return new EqualsBuilder()
                .append(assignedToMe, that.assignedToMe)
                .append(leadApplicant, that.leadApplicant)
                .append(daysLeft, that.daysLeft)
                .append(applicationProgress, that.applicationProgress)
                .append(assignedToInterview, that.assignedToInterview)
                .append(applicationState, that.applicationState)
                .append(projectState, that.projectState)
                .append(projectId, that.projectId)
                .append(endDate, that.endDate)
                .append(title, that.title)
                .append(applicationId, that.applicationId)
                .append(competitionTitle, that.competitionTitle)
                .append(dashboardSection, that.dashboardSection)
                .append(startDate, that.startDate)
                .append(collaborationLevelSingle, that.collaborationLevelSingle)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assignedToMe)
                .append(applicationState)
                .append(projectState)
                .append(projectId)
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
                .append(collaborationLevelSingle)
                .toHashCode();
    }

    public static class DashboardPreviousApplicationResourceBuilder {

        private String title;
        private long applicationId;
        private String competitionTitle;
        private boolean assignedToMe;
        private ApplicationState applicationState;
        private ProjectState projectState;
        private Long projectId;
        private boolean leadApplicant;
        private ZonedDateTime endDate;
        private long daysLeft;
        private int applicationProgress;
        private boolean assignedToInterview;
        private LocalDate startDate;
        private boolean collaborationLevelSingle;

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

        public DashboardPreviousApplicationResourceBuilder withAssignedToMe(boolean assignedToMe) {
            this.assignedToMe = assignedToMe;
            return this;
        }

        public DashboardPreviousApplicationResourceBuilder withApplicationState(ApplicationState applicationState) {
            this.applicationState = applicationState;
            return this;
        }

        public DashboardPreviousApplicationResourceBuilder withProjectState(ProjectState projectState) {
            this.projectState = projectState;
            return this;
        }

        public DashboardPreviousApplicationResourceBuilder withProjectId(Long projectId) {
            this.projectId = projectId;
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

        public DashboardPreviousApplicationResourceBuilder withCollaborationLevelSingle(boolean collaborationLevelSingle) {
            this.collaborationLevelSingle = collaborationLevelSingle;
            return this;
        }

        public DashboardPreviousRowResource build(){
            DashboardPreviousRowResource result = new DashboardPreviousRowResource();
            result.title = this.title;
            result.applicationId = this.applicationId;
            result.projectState = this.projectState;
            result.projectId = this.projectId;
            result.competitionTitle = this.competitionTitle;
            result.assignedToMe = this.assignedToMe;
            result.applicationState = this.applicationState;
            result.leadApplicant = this.leadApplicant;
            result.endDate = this.endDate;
            result.daysLeft = this.daysLeft;
            result.applicationProgress = this.applicationProgress;
            result.assignedToInterview = this.assignedToInterview;
            result.startDate = startDate;
            result.collaborationLevelSingle = this.collaborationLevelSingle;

            return result;
        }
    }

}