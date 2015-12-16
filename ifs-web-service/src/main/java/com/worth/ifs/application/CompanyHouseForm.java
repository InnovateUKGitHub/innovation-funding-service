package com.worth.ifs.application;

import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import com.worth.ifs.user.domain.OrganisationSize;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

/**
 * Object to store the data that is use form the company house form, while creating a new application.
 */
public class CompanyHouseForm  extends CreateApplicationForm{
    @NotEmpty
    // on empty value don't check pattern since then there already is a validation message.
    @Pattern(regexp = "^$|^[A-Za-z0-9_\\&-,.:;\\@]+$", message = "Please enter valid characters")
    private String companyHouseName;
    private List<CompanyHouseBusiness> companyHouseList;

    @NotEmpty
    private String postcodeInput;
    @NotEmpty
    private String organisationName;
    private String selectedPostcodeIndex;
    private Address selectedPostcode = null;
    private List<Address> postcodeOptions;
    @NotNull
    private OrganisationSize organisationSize;
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

    public Address getSelectedPostcode() {
        return selectedPostcode;
    }

    public void setSelectedPostcode(Address selectedPostcode) {
        this.selectedPostcode = selectedPostcode;
    }

    public List<Address> getPostcodeOptions() {
        return postcodeOptions;
    }

    public void setPostcodeOptions(List<Address> postcodeOptions) {
        this.postcodeOptions = postcodeOptions;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
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
}
