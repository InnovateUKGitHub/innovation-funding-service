package com.worth.ifs.organisation.resource;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.worth.ifs.address.resource.AddressResource;
/**
 * Resource object to store the company details, from the company house api.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CompanyHouseBusiness{
    private String companyNumber;
    private String name;
    private String type;
    private String dateOfCreation;
    private String description;
    @Valid
    private AddressResource officeAddress;

    public CompanyHouseBusiness() {
    	// no-arg constructor
    }

    public CompanyHouseBusiness(String companyNumber, String name, String type, String dateOfCreation, String description, AddressResource officeAddress) {
        this.companyNumber = companyNumber;
        this.name = name;
        this.type = type;
        this.dateOfCreation = dateOfCreation;
        this.description = description;
        this.officeAddress = officeAddress;
    }

    @JsonIgnore
    public String getLocation() {
        String locationString = "";
        locationString +=  officeAddress.getAddressLine1();
        locationString +=  ", "+ officeAddress.getTown();
        locationString +=  ", "+ officeAddress.getPostcode();
        return locationString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(String dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AddressResource getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(AddressResource officeAddress) {
        this.officeAddress = officeAddress;
    }
}
