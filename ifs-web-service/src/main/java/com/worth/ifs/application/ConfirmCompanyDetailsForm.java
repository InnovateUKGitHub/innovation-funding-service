package com.worth.ifs.application;

import com.worth.ifs.organisation.domain.Address;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

/**
 * Object to store the data that is use form the company house form, while creating a new application.
 */
public class ConfirmCompanyDetailsForm extends Form{
    private final Log log = LogFactory.getLog(getClass());

    @NotEmpty
    private String postcodeInput;
    private String selectedPostcodeIndex;
    private Address selectedPostcode = null;
    private List<Address> postcodeOptions;
    private String organisationSize;

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
                if(postcodeOptions == null || postcodeOptions.get(indexInt) == null){
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

    public String getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(String organisationSize) {
        this.organisationSize = organisationSize;
    }
}
