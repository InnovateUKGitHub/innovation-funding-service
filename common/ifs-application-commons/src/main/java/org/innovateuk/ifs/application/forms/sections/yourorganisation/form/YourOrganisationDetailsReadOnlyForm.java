package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSicCodeResource;

import java.util.ArrayList;
import java.util.List;

public class YourOrganisationDetailsReadOnlyForm {
    private String organisationName;
    private String registrationNumber;
    private AddressResource addressResource;
    private List<OrganisationSicCodeResource> sicCodes = new ArrayList<>();
    private boolean isOrgDetailedDisplayRequired;

    //SIC code
    //Registered address


    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public AddressResource getAddressResource() {
        return addressResource;
    }

    public void setAddressResource(AddressResource addressResource) {
        this.addressResource = addressResource;
    }

    public boolean isOrgDetailedDisplayRequired() {
        return isOrgDetailedDisplayRequired;
    }

    public void setOrgDetailedDisplayRequired(boolean orgDetailedDisplayRequired) {
        isOrgDetailedDisplayRequired = orgDetailedDisplayRequired;
    }

    public List<OrganisationSicCodeResource> getSicCodes() {
        return sicCodes;
    }

    public void setSicCodes(List<OrganisationSicCodeResource> sicCodes) {
        this.sicCodes = sicCodes;
    }

    @JsonIgnore
    public List<String> getRegisteredAddressString() {
        List<String> addressData = new ArrayList<String>();
        if (addressResource != null) {
            addressData.add(addressResource.getAddressLine1());
            addressData.add(addressResource.getTown());
            addressData.add(addressResource.getCounty());
            addressData.add(addressResource.getPostcode());
            return  addressData;
        }
       addressData.add("");
       return addressData;
    }

    @JsonIgnore
    public List<String> getSicCodesString() {
        List<String> sicCodeStrings = new ArrayList<String>();
        if (sicCodes != null) {
            for(OrganisationSicCodeResource sicCodeResource : sicCodes) {
                sicCodeStrings.add(sicCodeResource.getSicCode());
            }
            return sicCodeStrings;
        }
        sicCodeStrings.add("");
         return sicCodeStrings;
    }
}
