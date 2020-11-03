package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.util.Optional;


/**
 * View model to back the Partners' Assigned Monitoring Officer page
 */
public class LegacyMonitoringOfficerViewModel {

    private Long projectId;
    private Long applicationId;
    private String projectName;
    private boolean monitoringOfficerAssigned;
    private String monitoringOfficerName;
    private String monitoringOfficerEmailAddress;
    private String monitoringOfficerPhoneNumber;
    private boolean ktpCompetition;

    public LegacyMonitoringOfficerViewModel(ProjectResource project,
                                            Optional<MonitoringOfficerResource> monitoringOfficer,
                                            boolean ktpCompetition) {
        this.projectId = project.getId();
        this.applicationId = project.getApplication();
        this.projectName = project.getName();
        this.monitoringOfficerAssigned = monitoringOfficer.isPresent();
        this.monitoringOfficerName = monitoringOfficer.map(mo -> mo.getFullName()).orElse("");
        this.monitoringOfficerEmailAddress = monitoringOfficer.map(mo -> mo.getEmail()).orElse("");
        this.monitoringOfficerPhoneNumber = monitoringOfficer.map(mo -> mo.getPhoneNumber()).orElse("");
        this.ktpCompetition = ktpCompetition;
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

    public boolean getKtpCompetition() {
        return ktpCompetition;
    }
}
