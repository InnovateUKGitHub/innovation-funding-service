package org.innovateuk.ifs.organisation.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.commons.ZeroDowntime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparingLong;

public class OrganisationResource {
    private Long id;
    private String name;
    private String companiesHouseNumber;
    private List<Long> processRoles = new ArrayList<>();
    private List<Long> applicationFinances = new ArrayList<>();
    private List<OrganisationAddressResource> addresses = new ArrayList<>();
    private List<Long> users = new ArrayList<>();
    private Long organisationType;
    private String organisationTypeName;
    private String organisationTypeDescription;

    public static final Comparator<OrganisationResource> normalOrgComparator = comparingLong(OrganisationResource::getId);

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

    @ZeroDowntime(description = "remove", reference = "IFS-3195")
    @Deprecated
    public String getCompanyHouseNumber() {
        return getCompaniesHouseNumber();
    }

    public String getCompaniesHouseNumber() {
        return companiesHouseNumber;
    }

    public void setCompaniesHouseNumber(String companiesHouseNumber) {
        this.companiesHouseNumber = companiesHouseNumber;
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

    public String getOrganisationTypeName() {
        return organisationTypeName;
    }

    public void setOrganisationTypeName(String organisationTypeName) {
        this.organisationTypeName = organisationTypeName;
    }

    public String getOrganisationTypeDescription() {
        return organisationTypeDescription;
    }

    public void setOrganisationTypeDescription(String organisationTypeDescription) {
        this.organisationTypeDescription = organisationTypeDescription;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, false);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
