package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

public class MonitoringOfficerUnassignedProjectViewModel {
    private final long projectNumber;
    private final String projectName;

    public MonitoringOfficerUnassignedProjectViewModel(long projectNumber, String projectName) {
        this.projectNumber = projectNumber;
        this.projectName = projectName;
    }

    public long getProjectNumber() {
        return projectNumber;
    }

    public String getProjectName() {
        return projectName;
    }
}