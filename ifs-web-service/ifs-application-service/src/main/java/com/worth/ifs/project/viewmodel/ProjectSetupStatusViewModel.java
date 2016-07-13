package com.worth.ifs.project.viewmodel;

import com.worth.ifs.bankdetails.resource.BankDetailsResource;
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
    private BankDetailsResource bankDetails;
    private boolean isFunded;

    public ProjectSetupStatusViewModel(ProjectResource project, CompetitionResource competition, Optional<MonitoringOfficerResource> monitoringOfficerResource, Optional<BankDetailsResource> bankDetails, boolean isFunded) {
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.applicationId = project.getApplication();
        this.competitionName = competition.getName();
        this.projectDetailsSubmitted = project.isProjectDetailsSubmitted();
        this.monitoringOfficerAssigned = monitoringOfficerResource.isPresent();
        this.monitoringOfficerName = monitoringOfficerResource.map(mo -> mo.getFullName()).orElse("");
        this.bankDetails = bankDetails.orElse(null);
        this.isFunded = isFunded;
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

    public BankDetailsResource getBankDetails() {
        return bankDetails;
    }

    public boolean isFunded() {
        return isFunded;
    }
}
