package com.worth.ifs.organisation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.address.domain.Address;
import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.user.domain.Organisation;

import javax.persistence.*;
import javax.validation.Valid;

/**
 * Resource object to store the address details, from the company, from the company house api.
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint( columnNames = { "organisation_id", "address_id" } ) } )
public class OrganisationAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Organisation organisation;
    @Valid
    @ManyToOne(cascade = CascadeType.ALL)
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="address_type_id", referencedColumnName="id")
    private AddressType addressType;

    public OrganisationAddress(Organisation organisation, Address address, AddressType addressType) {
        this.organisation = organisation;
        this.address = address;
        this.addressType = addressType;
    }

    public OrganisationAddress() {
    	// no-arg constructor
    }

    @JsonIgnore
    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }


    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
