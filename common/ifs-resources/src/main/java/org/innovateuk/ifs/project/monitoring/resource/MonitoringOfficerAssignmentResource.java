package org.innovateuk.ifs.project.monitoring.resource;

import java.util.List;

public class MonitoringOfficerAssignmentResource {

    private final long userId;
    private final String firstName;
    private final String lastName;
    private final List<MonitoringOfficerUnassignedProjectResource> unassignedProjects;
    private final List<MonitoringOfficerAssignedProjectResource> assignedProjects;

    public MonitoringOfficerAssignmentResource() {
        userId = -1;
        firstName = null;
        lastName = null;
        unassignedProjects = null;
        assignedProjects = null;
    }

    public MonitoringOfficerAssignmentResource(long userId, String firstName, String lastName, List<MonitoringOfficerUnassignedProjectResource> unassignedProjects, List<MonitoringOfficerAssignedProjectResource> assignedProjects) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.unassignedProjects = unassignedProjects;
        this.assignedProjects = assignedProjects;
    }

    public long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public List<MonitoringOfficerUnassignedProjectResource> getUnassignedProjects() {
        return unassignedProjects;
    }

    public List<MonitoringOfficerAssignedProjectResource> getAssignedProjects() {
        return assignedProjects;
    }
}