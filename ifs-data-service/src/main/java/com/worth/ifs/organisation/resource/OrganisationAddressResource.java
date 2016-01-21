package com.worth.ifs.organisation.resource;

import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.user.domain.AddressType;
import com.worth.ifs.user.domain.Organisation;

public class OrganisationAddressResource {
    private Long id;
    private Long organisation;
    private Long address;
    private AddressType addressType;

    public OrganisationAddressResource(Organisation organisation, Address address, AddressType addressType) {
        this.organisation = organisation.getId();
        this.address = address.getId();
        this.addressType = addressType;
    }

    public OrganisationAddressResource() {
    }

    public Long getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation.getId();
    }


    public Long getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address.getId();
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

}
