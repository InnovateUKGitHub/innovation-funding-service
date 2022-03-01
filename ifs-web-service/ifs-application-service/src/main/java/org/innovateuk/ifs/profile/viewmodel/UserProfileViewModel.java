package org.innovateuk.ifs.profile.viewmodel;

import org.innovateuk.ifs.user.resource.EDIStatus;

import java.time.ZonedDateTime;
import java.util.Set;

/**
 * View model to show values on the user profile page
 */
public class UserProfileViewModel {

    private final String name;
    private final String phoneNumber;
    private final String emailAddress;
    private final boolean allowMarketingEmails;
    private final Set<OrganisationProfileViewModel> organisations;
    private final boolean monitoringOfficer;
    private final EDIStatus ediStatus;
    private final ZonedDateTime ediReviewDate;
    private boolean ediUpdateEnabled;
    private String ediUpdateUrl;

    public UserProfileViewModel(String name, String phoneNumber, String emailAddress, boolean allowMarketingEmails,
                                Set<OrganisationProfileViewModel> organisations, boolean monitoringOfficer,
                                EDIStatus ediStatus, ZonedDateTime ediReviewDate, boolean ediUpdateEnabled, String ediUpdateUrl) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.allowMarketingEmails = allowMarketingEmails;
        this.organisations = organisations;
        this.monitoringOfficer = monitoringOfficer;
        this.ediStatus = ediStatus;
        this.ediReviewDate = ediReviewDate;
        this.ediUpdateEnabled = ediUpdateEnabled;
        this.ediUpdateUrl = ediUpdateUrl;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public boolean isAllowMarketingEmails() {
        return allowMarketingEmails;
    }

    public Set<OrganisationProfileViewModel> getOrganisations() {
        return organisations;
    }

    public boolean isMonitoringOfficer() {
        return monitoringOfficer;
    }

    public boolean isEdiUpdateEnabled() { return ediUpdateEnabled; }

    public String getEdiUpdateUrl() { return ediUpdateUrl; }

    public boolean isEdiCompleted() { return EDIStatus.COMPLETE == ediStatus; }

    public boolean isEdiIncomplete() { return EDIStatus.INCOMPLETE == ediStatus; }

    public boolean isEdiInProgress() { return EDIStatus.INPROGRESS == ediStatus; }

    public EDIStatus getEdiStatus() { return ediStatus; }

    public ZonedDateTime getEdiReviewDate() {
        return ediReviewDate;
    }
}
