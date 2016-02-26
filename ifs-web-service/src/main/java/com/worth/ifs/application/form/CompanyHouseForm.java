package com.worth.ifs.application.form;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Object to store the data that is use form the company house form, while creating a new application.
 */
public class CompanyHouseForm extends CreateApplicationForm implements Serializable {
    @NotEmpty
    // on empty value don't check pattern since then there already is a validation message.
    @Pattern(regexp = "^$|^[A-Za-z0-9 _\\&-,.:;\\@]+$", message = "Please enter valid characters")
    private String companyHouseName;
    private boolean companyHouseSearching;
    private transient List<CompanyHouseBusiness> companyHouseList;

    @NotEmpty
    private String postcodeInput;
    @NotEmpty
    private String organisationName;
    private String selectedPostcodeIndex;
    @Valid
    private transient AddressResource selectedPostcode = null;
    @Valid
    private transient List<AddressResource> postcodeOptions;

    private boolean manualAddress = false;
    private boolean inCompanyHouse = true;

    public CompanyHouseForm() {
        this.companyHouseList = new ArrayList<>();
        this.postcodeOptions = new ArrayList<>();
    }
    public CompanyHouseForm(List<CompanyHouseBusiness> companyHouseList) {
        this.companyHouseList = companyHouseList;
        this.postcodeOptions = new ArrayList<>();
    }


    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public void setCompanyHouseList(List<CompanyHouseBusiness> companyHouseList) {
        this.companyHouseList = companyHouseList;
    }

    public List<CompanyHouseBusiness> getCompanyHouseList() {
        return companyHouseList;
    }


    public String getPostcodeInput() {
        return postcodeInput;
    }

    public void setPostcodeInput(String postcodeInput) {
        this.postcodeInput = postcodeInput;
    }

    public String getSelectedPostcodeIndex() {
        return selectedPostcodeIndex;
    }

    public void setSelectedPostcodeIndex(String selectedPostcodeIndex) {
        this.selectedPostcodeIndex = selectedPostcodeIndex;
    }

    public AddressResource getSelectedPostcode() {
        return selectedPostcode;
    }

    public void setSelectedPostcode(AddressResource selectedPostcode) {
        this.selectedPostcode = selectedPostcode;
    }

    public List<AddressResource> getPostcodeOptions() {
        return postcodeOptions;
    }

    public void setPostcodeOptions(List<AddressResource> postcodeOptions) {
        this.postcodeOptions = postcodeOptions;
    }

    public boolean isManualAddress() {
        return manualAddress;
    }

    public void setManualAddress(boolean manualAddress) {
        this.manualAddress = manualAddress;
    }

    public boolean isInCompanyHouse() {
        return inCompanyHouse;
    }

    public void setInCompanyHouse(boolean inCompanyHouse) {
        this.inCompanyHouse = inCompanyHouse;
    }

    public String getCompanyHouseName() {
        return companyHouseName;
    }

    public void setCompanyHouseName(String companyHouseName) {
        this.companyHouseName = companyHouseName;
    }

    public boolean isCompanyHouseSearching() {
        return companyHouseSearching;
    }

    public void setCompanyHouseSearching(boolean companyHouseSearching) {
        this.companyHouseSearching = companyHouseSearching;
    }
}
