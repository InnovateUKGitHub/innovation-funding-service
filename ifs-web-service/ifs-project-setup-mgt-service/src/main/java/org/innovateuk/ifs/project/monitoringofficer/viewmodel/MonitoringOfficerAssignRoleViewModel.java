package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

public class MonitoringOfficerAssignRoleViewModel {

    private long userId;
    private String firstName;
    private String lastName;
    private String emailAddress;

    public MonitoringOfficerAssignRoleViewModel(long userId,
                                                String firstName,
                                                String lastName,
                                                String emailAddress) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
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

    public String getEmailAddress() {
        return emailAddress;
    }

    private void setUserId(long userId) {
        this.userId = userId;
    }

    private void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    private void setLastName(String lastName) {
        this.lastName = lastName;
    }

    private void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}