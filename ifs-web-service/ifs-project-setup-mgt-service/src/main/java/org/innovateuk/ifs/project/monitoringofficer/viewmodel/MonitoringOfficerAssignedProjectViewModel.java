package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

public class MonitoringOfficerAssignedProjectViewModel {
    private final long projectId;
    private final long applicationId;
    private final long competitionId;

    private final String projectName;
    private final String leadOrganisationName;

    public MonitoringOfficerAssignedProjectViewModel(long projectId,
                                                     long applicationId,
                                                     long competitionId,
                                                     String projectName,
                                                     String leadOrganisationName) {
        this.applicationId = applicationId;
        this.competitionId = competitionId;
        this.projectId = projectId;
        this.projectName = projectName;
        this.leadOrganisationName = leadOrganisationName;
    }

    public long getProjectId() {
        return projectId;
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