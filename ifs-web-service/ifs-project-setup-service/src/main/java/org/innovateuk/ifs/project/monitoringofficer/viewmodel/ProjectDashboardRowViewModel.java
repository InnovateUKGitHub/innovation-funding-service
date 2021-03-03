package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * View model for each project row in the 'Project' section of the monitoring officer dashboard.
 */
public class ProjectDashboardRowViewModel {

    private final long applicationNumber;
    private final String competitionTitle;
    private final long projectId;
    private final String projectTitle;
    private final ProjectState projectState;

    public ProjectDashboardRowViewModel(ProjectResource project) {
        this.applicationNumber = project.getApplication();
        this.competitionTitle = project.getCompetitionName();
        this.projectId = project.getId();
        this.projectTitle = project.getName();
        this.projectState = project.getProjectState();
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
        return isNullOrEmpty(projectTitle) ? competitionTitle : projectTitle;
    }

    public long getApplicationNumber() {
        return applicationNumber;
    }

    public String getCompetitionTitle() {
        return competitionTitle;
    }

    public ProjectState getProjectState() {
        return projectState;
    }


    public boolean isWithdrawn() {
        return projectState.isWithdrawn();
    }

    public boolean isLiveOrCompletedOffline() {
        return projectState.isLive() || projectState.isCompletedOffline();
    }

    public boolean isUnsuccessful() {
        return projectState.isUnsuccessful();
    }
}
