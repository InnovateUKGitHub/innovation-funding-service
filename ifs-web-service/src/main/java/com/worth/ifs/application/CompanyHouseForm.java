package com.worth.ifs.application;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class CompanyHouseForm  extends Form{
    @NotEmpty
    private String organisationName;


    private List<CompanyHouse> companyHouseList;

    public CompanyHouseForm() {
        this.companyHouseList = new ArrayList<>();
    }
    public CompanyHouseForm(List<CompanyHouse> companyHouseList) {
        this.companyHouseList = companyHouseList;
    }


    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public void setCompanyHouseList(List<CompanyHouse> companyHouseList) {
        this.companyHouseList = companyHouseList;
    }

    public List<CompanyHouse> getCompanyHouseList() {
        return companyHouseList;
    }
}
