package org.innovateuk.ifs.organisation.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Comparator;

import static java.util.Comparator.comparingLong;

public class  OrganisationResource {
    private Long id;
    private String name;
    private String companiesHouseNumber;
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
