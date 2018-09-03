package org.innovateuk.ifs.eugrant.organisation.viewmodel;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.eugrant.EuOrganisationType;

public class EuOrganisationViewModel {

    private final EuOrganisationType type;
    private final String name;
    private final String registrationNumber;
    private final String addressLine1;
    private final String addressLine2;
    private final String addressLine3;
    private final String town;
    private final String county;
    private final String postcode;

    public EuOrganisationViewModel(EuOrganisationType type, String name) {
        this(type, name, null, new AddressResource());
    }

    public EuOrganisationViewModel(EuOrganisationType type, String name, String registrationNumber, AddressResource organisationAddress) {
        this.type = type;
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.addressLine1 = organisationAddress.getAddressLine1();
        this.addressLine2 = organisationAddress.getAddressLine2();
        this.addressLine3 = organisationAddress.getAddressLine3();
        this.town = organisationAddress.getTown();
        this.county = organisationAddress.getCounty();
        this.postcode = organisationAddress.getPostcode();
    }

    public EuOrganisationType getType() {
        return type;
    }

    public String getName() {
        return name;
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

    /* view model logic. */
    public boolean hasCompaniesHouseFields() {
        return !type.isResearch() && registrationNumber != null;
    }
}
