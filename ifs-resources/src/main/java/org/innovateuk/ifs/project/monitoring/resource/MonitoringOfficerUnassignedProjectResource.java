package org.innovateuk.ifs.project.monitoring.resource;

public class MonitoringOfficerUnassignedProjectResource {

    private final long projectId;
    private final long applicationId;
    private final String projectName;

    public MonitoringOfficerUnassignedProjectResource() {
        projectId = -1;
        applicationId = -1;
        projectName = null;
    }

    public MonitoringOfficerUnassignedProjectResource(long projectId, long applicationId, String projectName) {
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