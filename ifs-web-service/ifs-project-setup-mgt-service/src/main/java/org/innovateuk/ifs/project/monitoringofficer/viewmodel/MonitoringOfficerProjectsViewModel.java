package org.innovateuk.ifs.project.monitoringofficer.viewmodel;


import java.util.List;

public class MonitoringOfficerProjectsViewModel {

    private final long projectMonitoringOfficerId;
    private final String monitoringOfficerName;
    private final int assignedProjectsCount;
    private final List<MonitoringOfficerAssignedProjectViewModel> assignedProjects;
    private final List<MonitoringOfficerUnassignedProjectViewModel> unassignedProjects;

    public MonitoringOfficerProjectsViewModel(long projectMonitoringOfficerId,
                                              String monitoringOfficerName,
                                              int assignedProjectsCount,
                                              List<MonitoringOfficerAssignedProjectViewModel> assignedProjects,
                                              List<MonitoringOfficerUnassignedProjectViewModel> unassignedProjects) {
        this.projectMonitoringOfficerId = projectMonitoringOfficerId;
        this.monitoringOfficerName = monitoringOfficerName;
        this.assignedProjectsCount = assignedProjectsCount;
        this.assignedProjects = assignedProjects;
        this.unassignedProjects = unassignedProjects;
    }

    public long getProjectMonitoringOfficerId() {
        return projectMonitoringOfficerId;
    }

    public String getMonitoringOfficerName() {
        return monitoringOfficerName;
    }

    public int getAssignedProjectsCount() {
        return assignedProjectsCount;
    }

    public List<MonitoringOfficerAssignedProjectViewModel> getAssignedProjects() {
        return assignedProjects;
    }

    public List<MonitoringOfficerUnassignedProjectViewModel> getUnassignedProjects() {
        return unassignedProjects;
    }
}