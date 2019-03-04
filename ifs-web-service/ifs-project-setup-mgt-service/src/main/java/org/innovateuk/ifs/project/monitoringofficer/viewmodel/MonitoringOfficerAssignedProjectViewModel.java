package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

public class MonitoringOfficerAssignedProjectViewModel {
    private final long projectNumber;
    private final String projectName;
    private final String leadOrganisationName;

    public MonitoringOfficerAssignedProjectViewModel(long projectNumber, String projectName, String leadOrganisationName) {
        this.projectNumber = projectNumber;
        this.projectName = projectName;
        this.leadOrganisationName = leadOrganisationName;
    }

    public long getProjectNumber() {
        return projectNumber;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getLeadOrganisationName() {
        return leadOrganisationName;
    }
}