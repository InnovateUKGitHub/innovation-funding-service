package org.innovateuk.ifs.address.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.address.resource.AddressResource;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Resource object to store the address details, from the company, from the companies house api.
 */
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    
    private String town;
    private String county;
    private String country;

    private String postcode;

    public Address() {
        // no-arg constructor
    }

    public Address(Address address) {
        this(address.getAddressLine1(), address.getAddressLine2(), address.getAddressLine3(), address.getTown(), address.getCounty(), address.getPostcode(), address.getCountry());
    }

    public Address(String addressLine1, String addressLine2, String addressLine3, String town, String county, String postcode) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        this.town = town;
        this.county = county;
        this.postcode = postcode;
    }

    public Address(String addressLine1, String addressLine2, String addressLine3, String town, String county, String postcode, String country) {
        this(addressLine1, addressLine2, addressLine3, town, county, postcode);
        this.country = country;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postCode) {
        this.postcode = postCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void updateFrom(AddressResource other) {
        this.addressLine1 = other.getAddressLine1();
        this.addressLine2 = other.getAddressLine2();
        this.addressLine3 = other.getAddressLine3();
        this.town = other.getTown();
        this.county = other.getCounty();
        this.postcode = other.getPostcode();
        this.country = other.getCountry();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        return new EqualsBuilder()
                .append(addressLine1, address.addressLine1)
                .append(addressLine2, address.addressLine2)
                .append(addressLine3, address.addressLine3)
                .append(town, address.town)
                .append(county, address.county)
                .append(postcode, address.postcode)
                .append(country, address.country)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(addressLine1)
                .append(addressLine2)
                .append(addressLine3)
                .append(town)
                .append(county)
                .append(postcode)
                .append(country)
                .toHashCode();
    }

    public void copyFrom(AddressResource address) {
        this.addressLine1 = address.getAddressLine1();
        this.addressLine2 = address.getAddressLine2();
        this.addressLine3 = address.getAddressLine3();
        this.town = address.getTown();
        this.county = address.getCounty();
        this.postcode = address.getPostcode();
        this.country = address.getCountry();
    }
}
