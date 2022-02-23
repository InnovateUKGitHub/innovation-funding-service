package org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSicCodeResource;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class YourOrganisationDetailsReadOnlyViewModel {

    private String organisationName;
    private String organisationType;
    private String registrationNumber;
    private AddressResource addressResource;
    private List<OrganisationSicCodeResource> sicCodes = new ArrayList<>();
    private boolean isOrgDetailedDisplayRequired;

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
