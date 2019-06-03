package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

/**
 * Holder of values for each row on the View all monitoring officers page
 */
public class MonitoringOfficerViewRow {

    private final String firstName;

    private final String lastName;

    private final long userId;

    public MonitoringOfficerViewRow(String firstName, String lastName, long userId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;
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
}
