package org.innovateuk.ifs.project.monitoring.resource;

public class MonitoringOfficerUnassignedProjectResource {

    private final long projectId;
    private final String projectName;

    public MonitoringOfficerUnassignedProjectResource() {
        projectId = -1;
        projectName = null;
    }

    public MonitoringOfficerUnassignedProjectResource(long projectId, String projectName) {
        this.projectId = projectId;
        this.projectName = projectName;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }
}