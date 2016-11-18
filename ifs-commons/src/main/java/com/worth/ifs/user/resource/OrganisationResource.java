package com.worth.ifs.user.resource;

import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class OrganisationResource {
    private Long id;
    private String name;
    private String companyHouseNumber;
    private OrganisationSize organisationSize;
    private List<Long> processRoles = new ArrayList<>();
    private List<Long> applicationFinances = new ArrayList<>();
    private List<OrganisationAddressResource> addresses = new ArrayList<>();
    private List<Long> users = new ArrayList<>();
    private Long organisationType;
    private String organisationTypeName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyHouseNumber() {
        return companyHouseNumber;
    }

    public void setCompanyHouseNumber(String companyHouseNumber) {
        this.companyHouseNumber = companyHouseNumber;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public List<Long> getProcessRoles() {
        return processRoles;
    }

    public void setProcessRoles(List<Long> processRoles) {
        this.processRoles = processRoles;
    }

    public List<Long> getApplicationFinances() {
        return applicationFinances;
    }

    public void setApplicationFinances(List<Long> applicationFinances) {
        this.applicationFinances = applicationFinances;
    }

    public List<OrganisationAddressResource> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<OrganisationAddressResource> addresses) {
        this.addresses = addresses;
    }

    public Long getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(Long organisationType) {
        this.organisationType = organisationType;
    }

    public List<Long> getUsers() {
        return users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, false);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public String getOrganisationTypeName() {
        return organisationTypeName;
    }

    public void setOrganisationTypeName(String organisationTypeName) {
        this.organisationTypeName = organisationTypeName;
    }
}
