package com.worth.ifs.address.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.organisation.domain.OrganisationAddress;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Resource object to store the address details, from the company, from the company house api.
 */
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    
    private String town;
    private String county;
    
    @Length(max = 9)
    private String postcode;

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL)
    private List<OrganisationAddress> organisations = new ArrayList<>();

    public Address() {
    	// no-arg constructor
    }

    public Address(String addressLine1, String addressLine2, String addressLine3, String town, String county, String postcode) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        this.town = town;
        this.county = county;
        this.postcode = postcode;
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

    public Long getId() {
        return id;
    }

    @JsonIgnore
    public List<OrganisationAddress> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<OrganisationAddress> organisations) {
        this.organisations = organisations;
    }

    public void setId(Long id) {
        this.id = id;
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
                .append(organisations, address.organisations)
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
                .append(organisations)
                .toHashCode();
    }
}
