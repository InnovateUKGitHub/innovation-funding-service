package org.innovateuk.ifs.profile.viewmodel;

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

    public UserProfileViewModel(String name, String phoneNumber, String emailAddress, boolean allowMarketingEmails, Set<OrganisationProfileViewModel> organisations, boolean monitoringOfficer) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.allowMarketingEmails = allowMarketingEmails;
        this.organisations = organisations;
        this.monitoringOfficer = monitoringOfficer;
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
}
