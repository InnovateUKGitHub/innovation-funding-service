package org.innovateuk.ifs.registration.viewmodel;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;

/**
 * View model for invited organisation confirmation
 */
public class ConfirmOrganisationInviteOrganisationViewModel {
    private String partOfOrganisation;
    private String organisationType;
    private String registrationName;
    private String registrationNumber;
    private Long organisationTypeId;
    private String emailLeadApplicant;
    private AddressResource organisationAddress;
    private String registerUrl;

    public ConfirmOrganisationInviteOrganisationViewModel(ApplicationInviteResource inviteResource, OrganisationResource organisation, AddressResource organisationAddress, String registerUrl) {
        this.partOfOrganisation = inviteResource.getInviteOrganisationNameConfirmedSafe();
        this.organisationType = organisation.getOrganisationTypeName();
        this.organisationTypeId = organisation.getOrganisationType();
        this.registrationName = organisation.getName();
        this.registrationNumber = organisation.getCompanyHouseNumber();
        this.emailLeadApplicant = inviteResource.getLeadApplicantEmail();
        this.organisationAddress = organisationAddress;
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

    public Boolean getRegistrationNumberNotEmptyAndNotResearch() {
        return !registrationNumber.isEmpty() && !OrganisationTypeEnum.isResearch(organisationTypeId);
    }

    public String getEmailLeadApplicant() {
        return emailLeadApplicant;
    }

    public AddressResource getOrganisationAddress() {
        return organisationAddress;
    }

    public String getRegisterUrl() {
        return registerUrl;
    }
}
