package com.worth.ifs.application;

import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

/**
 * Object to store the data that is use form the company house form, while creating a new application.
 */
public class CompanyHouseForm  extends Form{
    @NotEmpty
    private String organisationName;
    private List<CompanyHouseBusiness> companyHouseList;

    public CompanyHouseForm() {
        this.companyHouseList = new ArrayList<>();
    }
    public CompanyHouseForm(List<CompanyHouseBusiness> companyHouseList) {
        this.companyHouseList = companyHouseList;
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
}
