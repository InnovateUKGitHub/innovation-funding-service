package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

public class MonitoringOfficerAssignedProjectViewModel {
    private final long projectNumber;
    private final long applicationId;
    private final long competitionId;

    private final String projectName;
    private final String leadOrganisationName;

    public MonitoringOfficerAssignedProjectViewModel(long projectNumber,
                                                     long applicationId,
                                                     long competitionId,
                                                     String projectName,
                                                     String leadOrganisationName) {
        this.applicationId = applicationId;
        this.competitionId = competitionId;
        this.projectNumber = projectNumber;
        this.projectName = projectName;
        this.leadOrganisationName = leadOrganisationName;
    }

    public long getProjectNumber() {
        return projectNumber;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getLeadOrganisationName() {
        return leadOrganisationName;
    }
}