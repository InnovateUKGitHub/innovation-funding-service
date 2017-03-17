package org.innovateuk.ifs.profile.viewmodel;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.Optional;

import static org.innovateuk.ifs.util.ProfileUtil.getAddress;

/**
 * View model to show values on the user profile page
 */
public class UserDetailsViewModel {
    private String organisationName;
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

    public UserDetailsViewModel(final UserResource user, final OrganisationResource organisation) {
        if(organisation != null) {
            this.organisationName = organisation.getName();
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

        this.name = user.getName();
        this.phoneNumber = user.getPhoneNumber();
        this.emailAddress = user.getEmail();

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

    private String ifEmptyReturnAText(String string) {
        return string != null && !string.isEmpty() ? string : "None selected";
    }
}
