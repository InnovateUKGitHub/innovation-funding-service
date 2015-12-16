package com.worth.ifs.application;

import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.user.domain.OrganisationSize;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

/**
 * Object to store the data that is use form the company house form, while creating a new application.
 */
public class ConfirmCompanyDetailsForm extends CreateApplicationForm{
    private final Log log = LogFactory.getLog(getClass());

    @NotEmpty
    private String postcodeInput;
    private String selectedPostcodeIndex;
    private Address selectedPostcode = null;
    private List<Address> postcodeOptions;
    private OrganisationSize organisationSize;
    private boolean useCompanyHouseAddress = false;
    private boolean manualAddress = false;

    public ConfirmCompanyDetailsForm() {
        postcodeOptions = new ArrayList<>();
    }

    public String getPostcodeInput() {
        return postcodeInput;
    }

    public void setPostcodeInput(String postcodeInput) {
        this.postcodeInput = postcodeInput;
    }

    public List<Address> getPostcodeOptions() {
        return postcodeOptions;
    }

    public void setPostcodeOptions(List<Address> postcodeOptions) {
        this.postcodeOptions = postcodeOptions;
    }

    public String getSelectedPostcodeIndex() {
        return selectedPostcodeIndex;
    }

    public void setSelectedPostcodeIndex(String selectedPostcodeIndex) {
        this.selectedPostcodeIndex = selectedPostcodeIndex;
    }

    public Address getSelectedPostcode() {
        if(selectedPostcode == null){
            if(getSelectedPostcodeIndex() == null || getSelectedPostcodeIndex().equals("")){
                log.warn("Returning new postcode a");
                selectedPostcode = new Address();
            }else{
                int indexInt = Integer.parseInt(getSelectedPostcodeIndex());
                if(postcodeOptions == null || postcodeOptions.size() <= indexInt ||postcodeOptions.get(indexInt) == null){
                    log.warn("Returning new postcode b");
                    return new Address();
                }else{
                    selectedPostcode = postcodeOptions.get(indexInt);
                }
            }
        }
        if(selectedPostcode == null){
            log.warn("Returning null postcode");
        }
        return selectedPostcode;
    }

    public void setSelectedPostcode(Address selectedPostcode) {
        this.selectedPostcode = selectedPostcode;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public boolean isUseCompanyHouseAddress() {
        return useCompanyHouseAddress;
    }

    public void setUseCompanyHouseAddress(boolean useCompanyHouseAddress) {
        this.useCompanyHouseAddress = useCompanyHouseAddress;
    }

    public boolean isManualAddress() {
        return manualAddress;
    }

    public void setManualAddress(boolean manualAddress) {
        this.manualAddress = manualAddress;
    }
}
