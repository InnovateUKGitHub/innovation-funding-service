package org.innovateuk.ifs.project.monitoring.resource;

public class MonitoringOfficerAssignedProjectResource {

    private final long projectId;
    private final long applicationId;
    private final long competitionId;

    private final String projectName;
    private final String leadOrganisationName;

    public MonitoringOfficerAssignedProjectResource() {
        projectId = -1;
        applicationId = -1;
        competitionId = -1;
        projectName = null;
        leadOrganisationName = null;
    }

    public MonitoringOfficerAssignedProjectResource(long projectId, long applicationId, long competitionId, String projectName, String leadOrganisationName) {
        this.projectId = projectId;
        this.applicationId = applicationId;
        this.competitionId = competitionId;
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