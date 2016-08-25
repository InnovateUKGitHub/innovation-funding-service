package com.worth.ifs.sil.experian.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Address object used to generate json for SIL.  Nulls are replaced with empty strings.
 */
public class Address {
    private String organisation;
    private String buildingName;
    private String street;
    private String locality;
    private String town;
    private String postcode;

    public Address() {}

    public Address(String organisation, String buildingName, String street, String locality, String town, String postcode) {
        this.organisation = organisation == null ? "" : organisation;
        this.buildingName = buildingName == null ? "" : buildingName;
        this.street = street == null ? "" : street;
        this.locality = locality == null ? "" : locality;
        this.town = town == null ? "" : town;
        this.postcode = postcode == null ? "" : postcode;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation == null ? "" : organisation;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName == null ? "" : buildingName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street == null ? "" : street;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality == null ? "" : locality;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town == null ? "" : town;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode == null ? "" : postcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        return new EqualsBuilder()
                .append(organisation, address.organisation)
                .append(buildingName, address.buildingName)
                .append(street, address.street)
                .append(locality, address.locality)
                .append(town, address.town)
                .append(postcode, address.postcode)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(organisation)
                .append(buildingName)
                .append(street)
                .append(locality)
                .append(town)
                .append(postcode)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Address{" +
                "organisation='" + organisation + '\'' +
                ", buildingName='" + buildingName + '\'' +
                ", street='" + street + '\'' +
                ", locality='" + locality + '\'' +
                ", town='" + town + '\'' +
                ", postcode='" + postcode + '\'' +
                '}';
    }
}
