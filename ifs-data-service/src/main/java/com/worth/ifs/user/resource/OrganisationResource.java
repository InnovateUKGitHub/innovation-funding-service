package com.worth.ifs.user.resource;

import com.worth.ifs.user.domain.OrganisationSize;
import org.springframework.hateoas.core.Relation;

import java.util.ArrayList;
import java.util.List;

@Relation(value="organisation", collectionRelation="organisations")
public class OrganisationResource {
    private Long id;
    private String name;
    private String companyHouseNumber;
    private OrganisationSize organisationSize;
    private List<Long> processRoles = new ArrayList<>();
    private List<Long> applicationFinances = new ArrayList<>();
    private List<Long> users = new ArrayList<>();
    private List<Long> addresses = new ArrayList<>();
    private Long organisationType;

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

    public List<Long> getUsers() {
        return users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }

    public List<Long> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Long> addresses) {
        this.addresses = addresses;
    }

    public Long getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(Long organisationType) {
        this.organisationType = organisationType;
    }
}
