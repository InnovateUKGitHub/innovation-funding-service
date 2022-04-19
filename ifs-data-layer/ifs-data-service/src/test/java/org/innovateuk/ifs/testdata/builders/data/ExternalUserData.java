package org.innovateuk.ifs.testdata.builders.data;

import org.innovateuk.ifs.user.resource.EDIStatus;
import org.innovateuk.ifs.user.resource.Role;

import java.time.LocalDate;

/**
 * Running data context for generating Applicants
 */
public class ExternalUserData extends BaseUserData {

    private String firstName;
    private String lastName;
    private String emailAddress;
    private Role role;
    private String organisation;
    private EDIStatus ediStatus;
    private LocalDate ediReviewDate;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public EDIStatus getEdiStatus() {
        return ediStatus;
    }

    public void setEdiStatus(EDIStatus ediStatus) {
        this.ediStatus = ediStatus;
    }

    public LocalDate getEdiReviewDate() {
        return ediReviewDate;
    }

    public void setEdiReviewDate(LocalDate ediReviewDate) {
        this.ediReviewDate = ediReviewDate;
    }
}
