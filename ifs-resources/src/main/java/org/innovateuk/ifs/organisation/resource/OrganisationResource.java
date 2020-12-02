package org.innovateuk.ifs.organisation.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparingLong;

public class OrganisationResource {
    private Long id;
    private String name;
    private String companiesHouseNumber;
    private Long organisationType;
    private String organisationTypeName;
    private String organisationTypeDescription;
    private boolean isInternational;
    private String internationalRegistrationNumber;
    private String registrationNumber;
    private List<OrganisationAddressResource> addresses = new ArrayList<>();
    private LocalDate dateOfIncorporation;
    private List<OrganisationSicCodeResource> sicCodes = new ArrayList<>();
    private String organisationNumber;
    private List<OrganisationExecutiveOfficerResource> executiveOfficers = new ArrayList<>();



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

    public String getCompaniesHouseNumber() {
        return companiesHouseNumber;
    }

    public void setCompaniesHouseNumber(String companiesHouseNumber) {
        this.companiesHouseNumber = companiesHouseNumber;
    }

    public Long getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(Long organisationType) {
        this.organisationType = organisationType;
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

    public boolean isInternational() {
        return isInternational;
    }

    public void setInternational(boolean international) {
        isInternational = international;
    }

    public String getInternationalRegistrationNumber() {
        return internationalRegistrationNumber;
    }

    public void setInternationalRegistrationNumber(String internationalRegistrationNumber) {
        this.internationalRegistrationNumber = internationalRegistrationNumber;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public List<OrganisationAddressResource> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<OrganisationAddressResource> addresses) {
        this.addresses = addresses;
    }

    public LocalDate getDateOfIncorporation() {
        return dateOfIncorporation;
    }

    public void setDateOfIncorporation(LocalDate dateOfIncorporation) {
        this.dateOfIncorporation = dateOfIncorporation;
    }

    public List<OrganisationSicCodeResource> getSicCodes() {
        return sicCodes;
    }

    public void setSicCodes(List<OrganisationSicCodeResource> sicCodes) {
        this.sicCodes = sicCodes;
    }

    public String getOrganisationNumber() {
        return organisationNumber;
    }

    public void setOrganisationNumber(String organistionNumber) {
        this.organisationNumber = organistionNumber;
    }

    public List<OrganisationExecutiveOfficerResource> getExecutiveOfficers() {
        return executiveOfficers;
    }

    public void setExecutiveOfficers(List<OrganisationExecutiveOfficerResource> executiveOfficers) {
        this.executiveOfficers = executiveOfficers;
    }

    @JsonIgnore
    public OrganisationTypeEnum getOrganisationTypeEnum() {
        return OrganisationTypeEnum.getFromId(organisationType);
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
