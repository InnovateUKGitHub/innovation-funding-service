package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

public class MonitoringOfficerAssignRoleViewModel {

    private long userId;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;

    public MonitoringOfficerAssignRoleViewModel(long userId,
                                                String firstName,
                                                String lastName,
                                                String emailAddress,
                                                String phoneNumber) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
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

    public String getPhoneNumber() {
        return phoneNumber;
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

    private void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}