package org.innovateuk.ifs.project.organisationdetails.viewmodel;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

public class OrganisationDetailsViewModel {

    private final long projectId;
    private final String projectName;
    private final String organisationType;
    private final String organisationName;
    private final String registrationNumber;
    private final String addressLine1;
    private final String addressLine2;
    private final String addressLine3;
    private final String town;
    private final String county;
    private final String postcode;

    private final YourOrganisationViewModel yourOrganisationViewModel;

    public OrganisationDetailsViewModel(long projectId, String projectName, OrganisationResource organisation, YourOrganisationViewModel yourOrganisationViewModel) {
        this.projectId = projectId;
        this.organisationType = organisation.getOrganisationTypeName();
        this.projectName = projectName;
        this.organisationName = organisation.getName();
        this.registrationNumber = organisation.getCompaniesHouseNumber();
        AddressResource address = organisation.getAddresses().get(0).getAddress();
        this.addressLine1 = address.getAddressLine1();
        this.addressLine2 = address.getAddressLine2();
        this.addressLine3 = address.getAddressLine3();
        this.town = address.getTown();
        this.county = address.getTown();
        this.postcode = address.getPostcode();

        this.yourOrganisationViewModel = yourOrganisationViewModel;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getOrganisationType() {
        return organisationType;
    }

    public String getProjectName() {
        return projectName;
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

    public boolean getReadOnly() {
        return true;
    }

    public YourOrganisationViewModel getYourOrganisationViewModel() {
        return yourOrganisationViewModel;
    }
}