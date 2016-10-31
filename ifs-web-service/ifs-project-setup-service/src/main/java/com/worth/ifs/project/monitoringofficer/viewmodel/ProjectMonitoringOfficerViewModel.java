package com.worth.ifs.project.monitoringofficer.viewmodel;

import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;

import java.util.Optional;


/**
 * View model to back the Partners' Assigned Monitoring Officer page
 */
public class ProjectMonitoringOfficerViewModel {

    private Long projectId;
    private Long applicationId;
    private String projectName;
    private boolean monitoringOfficerAssigned;
    private String monitoringOfficerName;
    private String monitoringOfficerEmailAddress;
    private String monitoringOfficerPhoneNumber;

    public ProjectMonitoringOfficerViewModel(ProjectResource project, Optional<MonitoringOfficerResource> monitoringOfficer) {
        this.projectId = project.getId();
        this.applicationId = project.getApplication();
        this.projectName = project.getName();
        this.monitoringOfficerAssigned = monitoringOfficer.isPresent();
        this.monitoringOfficerName = monitoringOfficer.map(mo -> mo.getFullName()).orElse("");
        this.monitoringOfficerEmailAddress = monitoringOfficer.map(mo -> mo.getEmail()).orElse("");
        this.monitoringOfficerPhoneNumber = monitoringOfficer.map(mo -> mo.getPhoneNumber()).orElse("");
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public boolean isMonitoringOfficerAssigned() {
        return monitoringOfficerAssigned;
    }

    public String getMonitoringOfficerName() {
        return monitoringOfficerName;
    }

    public String getMonitoringOfficerEmailAddress() {
        return monitoringOfficerEmailAddress;
    }

    public String getMonitoringOfficerPhoneNumber() {
        return monitoringOfficerPhoneNumber;
    }

    public Long getApplicationId() {
        return applicationId;
    }
}
