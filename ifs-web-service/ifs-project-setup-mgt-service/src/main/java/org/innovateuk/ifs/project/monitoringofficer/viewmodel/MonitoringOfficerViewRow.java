package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

public class MonitoringOfficerViewRow {

    private final String firstName;

    private final String lastName;

    private final long userId;

    private final long assignedProjects;

    public MonitoringOfficerViewRow(String firstName, String lastName, long userId, long assignedProjects) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;
        this.assignedProjects = assignedProjects;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public long getUserId() {
        return userId;
    }

    public long getAssignedProjects() {
        return assignedProjects;
    }
}
