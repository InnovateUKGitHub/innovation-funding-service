package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

public class MonitoringOfficerUnassignedProjectViewModel {
    private final long projectId;
    private final long applicationId;
    private final String projectName;

    public MonitoringOfficerUnassignedProjectViewModel(long projectId, long applicationId, String projectName) {
        this.projectId = projectId;
        this.applicationId = applicationId;
        this.projectName = projectName;
    }

    public long getProjectId() {
        return projectId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getProjectName() {
        return projectName;
    }
}