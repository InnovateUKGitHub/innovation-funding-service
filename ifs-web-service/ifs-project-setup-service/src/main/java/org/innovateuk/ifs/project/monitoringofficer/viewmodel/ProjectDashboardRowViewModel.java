package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * View model for each project row in the 'Project' section of the monitoring officer dashboard.
 */
public class ProjectDashboardRowViewModel {

    private final long applicationNumber;
    private final String competitionTitle;
    private final long projectId;
    private final String projectTitle;

    public ProjectDashboardRowViewModel(long applicationNumber, String competitionTitle, long projectId, String projectTitle) {
        this.applicationNumber = applicationNumber;
        this.competitionTitle = competitionTitle;
        this.projectId = projectId;
        this.projectTitle = projectTitle;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public String getLinkUrl() {
        return String.format("/project-setup/project/%s", projectId);
    }

    public String getTitle() {
        return !isNullOrEmpty(projectTitle) ? projectTitle : competitionTitle;
    }

    public long getApplicationNumber() {
        return applicationNumber;
    }

    public String getCompetitionTitle() {
        return competitionTitle;
    }
}
