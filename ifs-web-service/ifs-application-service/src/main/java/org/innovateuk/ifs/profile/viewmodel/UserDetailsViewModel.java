package org.innovateuk.ifs.profile.viewmodel;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.user.resource.EthnicityResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.util.ProfileUtil.getAddress;

/**
 * View model to show values on the user profile page
 */
public class UserDetailsViewModel {
    private String organisationName;
    private String organisationTypeName;
    private String registrationNumber;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String town;
    private String county;
    private String postcode;
    private String name;
    private String phoneNumber;
    private String emailAddress;
    private String ethnicity;
    private String gender;
    private String disability;
    private boolean allowMarketingEmails;

    public UserDetailsViewModel(final UserResource user, final OrganisationResource organisation, final List<EthnicityResource> ethnicityResourceList) {
        if(organisation != null) {
            this.organisationName = organisation.getName();
            this.organisationTypeName = organisation.getOrganisationTypeName();
            this.registrationNumber = organisation.getCompanyHouseNumber();
            Optional<OrganisationAddressResource> organisationAddress = getAddress(organisation);

            if (organisationAddress.isPresent() && organisationAddress.get().getAddress() != null) {
                AddressResource address = organisationAddress.get().getAddress();

                this.addressLine1 = address.getAddressLine1();
                this.addressLine2 = address.getAddressLine2();
                this.addressLine3 = address.getAddressLine3();
                this.county = address.getCounty();
                this.postcode = address.getPostcode();
                this.town = address.getTown();
            }
        }

        if(user.getTitle() != null) {
            this.name = user.getTitle() + " " + user.getName().trim();
        } else {
            this.name = user.getName();
        }
        this.phoneNumber = user.getPhoneNumber();
        this.emailAddress = user.getEmail();

        setEthnicityName(user.getEthnicity(), ethnicityResourceList);
        if(user.getGender() != null) {
            this.gender = user.getGender().getDisplayName();
        }
        if(user.getDisability() != null) {
            this.disability = user.getDisability().getDisplayName();
        }
        this.allowMarketingEmails = user.getAllowMarketingEmails();
    }

    private void setEthnicityName(final Long ethnicityId, final List<EthnicityResource> ethnicityResourceList) {
        ethnicityResourceList.stream().filter(ethnicityResource -> ethnicityResource.getId().equals(ethnicityId))
                .findAny()
                .ifPresent(ethnicityResource -> this.ethnicity = ethnicityResource.getDescription());
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public String getTown() {
        return town;
    }

    public String getCounty() {
        return county;
    }

    public String getPostcode() {
        return postcode;
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

    public String getEthnicity() {
        return ifEmptyReturnAText(ethnicity);
    }

    public String getGender() {
        return ifEmptyReturnAText(gender);
    }

    public String getDisability() {
        return ifEmptyReturnAText(disability);
    }

    public String getOrganisationTypeName() {
        return organisationTypeName;
    }

    public boolean getAllowMarketingEmails() {
        return allowMarketingEmails;
    }

    private String ifEmptyReturnAText(String string) {
        return string != null && !string.isEmpty() ? string : "None selected";
    }
}
