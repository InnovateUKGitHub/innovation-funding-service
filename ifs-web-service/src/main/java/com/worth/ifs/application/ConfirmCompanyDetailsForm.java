package com.worth.ifs.application;

import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import com.worth.ifs.organisation.resource.PostalAddress;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

/**
 * Object to store the data that is use form the company house form, while creating a new application.
 */
public class ConfirmCompanyDetailsForm extends Form{
    @NotEmpty
    private String postcodeInput;
    private String selectedPostcodeIndex;
    private List<PostalAddress> postcodeOptions;

    public ConfirmCompanyDetailsForm() {
        postcodeOptions = new ArrayList<>();
    }

    public String getPostcodeInput() {
        return postcodeInput;
    }

    public void setPostcodeInput(String postcodeInput) {
        this.postcodeInput = postcodeInput;
    }

    public List<PostalAddress> getPostcodeOptions() {
        return postcodeOptions;
    }

    public void setPostcodeOptions(List<PostalAddress> postcodeOptions) {
        this.postcodeOptions = postcodeOptions;
    }

    public String getSelectedPostcodeIndex() {
        return selectedPostcodeIndex;
    }

    public void setSelectedPostcodeIndex(String selectedPostcodeIndex) {
        this.selectedPostcodeIndex = selectedPostcodeIndex;
    }

    public PostalAddress getSelectedPostcode() {
        if(getSelectedPostcodeIndex() == null || getSelectedPostcodeIndex() == ""){
            return null;
        }
        int indexInt = Integer.parseInt(getSelectedPostcodeIndex());
        if(postcodeOptions == null || postcodeOptions.get(indexInt) == null){
            return null;
        }else{
            return postcodeOptions.get(indexInt);
        }
    }
}
