package com.worth.ifs.organisation.resource;

import com.worth.ifs.address.resource.AddressType;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.user.domain.Organisation;

public class OrganisationAddressResource {
    private Long id;
    private Long organisation;
    private AddressResource address;
    private AddressType addressType;

    public OrganisationAddressResource(Organisation organisation, AddressResource address, AddressType addressType) {
        this.organisation = organisation.getId();
        this.address = address;
        this.addressType = addressType;
    }

    public OrganisationAddressResource() {
    	// no-arg constructor
    }

    public Long getOrganisation() {
        return organisation;
    }

    public AddressResource getAddress() {
        return address;
    }

    public void setAddress(AddressResource address) {
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

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
    }
}
