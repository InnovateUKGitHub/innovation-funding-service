package com.worth.ifs.organisation.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.worth.ifs.commons.resource.ResourceWithEmbeddeds;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CompanyHouseBusiness extends ResourceWithEmbeddeds{
    private String companyNumber;
    private String name;
    private String type;
    private String dateOfCreation;
    private String description;
    private PostalAddress officeAddress;

    public CompanyHouseBusiness() {
    }

    public CompanyHouseBusiness(String companyNumber, String name, String type, String dateOfCreation, String description, PostalAddress officeAddress) {
        this.companyNumber = companyNumber;
        this.name = name;
        this.type = type;
        this.dateOfCreation = dateOfCreation;
        this.description = description;
        this.officeAddress = officeAddress;
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

    public PostalAddress getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(PostalAddress officeAddress) {
        this.officeAddress = officeAddress;
    }

    @JsonIgnore
    public String getLocation() {
        String locationString = "";
        locationString +=  officeAddress.getAddressLine1();
        locationString +=  ", "+ officeAddress.getLocality();
        locationString +=  ", "+ officeAddress.getPostalCode();
        return locationString;
    }


}
