package org.innovateuk.ifs.registration.viewmodel;

/**
 * View model for invited organisation confirmation
 */
public class ConfirmOrganisationInviteOrganisationViewModel {
    private String partOfOrganisation;
    private String organisationType;
    private String registrationName;
    private String registrationNumber;
    private String leadApplicantEmail;
    private boolean showRegistrationNumber;
    private boolean leadOrganisation;
    private String registerUrl;

    public ConfirmOrganisationInviteOrganisationViewModel(final String partOfOrganisation,
                                                          final String organisationType,
                                                          final String registrationName,
                                                          final String registrationNumber,
                                                          final String leadApplicantEmail,
                                                          final boolean showRegistrationNumber,
                                                          final boolean leadOrganisation,
                                                          final String registerUrl) {
        this.partOfOrganisation = partOfOrganisation;
        this.organisationType = organisationType;
        this.registrationName = registrationName;
        this.registrationNumber = registrationNumber;
        this.leadApplicantEmail = leadApplicantEmail;
        this.showRegistrationNumber = showRegistrationNumber;
        this.leadOrganisation = leadOrganisation;
        this.registerUrl = registerUrl;
    }

    public String getPartOfOrganisation() {
        return partOfOrganisation;
    }

    public String getOrganisationType() {
        return organisationType;
    }

    public String getRegistrationName() {
        return registrationName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getLeadApplicantEmail() {
        return leadApplicantEmail;
    }

    public boolean isShowRegistrationNumber() {
        return showRegistrationNumber;
    }

    public boolean isLeadOrganisation() {
        return leadOrganisation;
    }

    public String getRegisterUrl() {
        return registerUrl;
    }
}