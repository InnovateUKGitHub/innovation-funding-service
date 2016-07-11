package com.worth.ifs.project.viewmodel;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;

import java.util.Optional;

/**
 * A view model that backs the Project Status page
 */
public class ProjectSetupStatusViewModel {

    private Long projectId;
    private String projectName;
    private Long applicationId;
    private String competitionName;
    private boolean projectDetailsSubmitted;
    private boolean monitoringOfficerAssigned;
    private String monitoringOfficerName;

    public ProjectSetupStatusViewModel(ProjectResource project, CompetitionResource competition, Optional<MonitoringOfficerResource> monitoringOfficerResource) {
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.applicationId = project.getApplication();
        this.competitionName = competition.getName();
        this.projectDetailsSubmitted = project.isProjectDetailsSubmitted();
        this.monitoringOfficerAssigned = monitoringOfficerResource.isPresent();
        this.monitoringOfficerName = monitoringOfficerResource.map(mo -> mo.getFullName()).orElse("");
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public boolean isProjectDetailsSubmitted() {
        return projectDetailsSubmitted;
    }

    public boolean isMonitoringOfficerAssigned() {
        return monitoringOfficerAssigned;
    }

    public String getMonitoringOfficerName() {
        return monitoringOfficerName;
    }
}
