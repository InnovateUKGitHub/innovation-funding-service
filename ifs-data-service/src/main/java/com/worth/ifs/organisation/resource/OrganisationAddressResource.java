package com.worth.ifs.organisation.resource;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.AddressTypeResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class OrganisationAddressResource {
    private Long id;
    private Long organisation;
    private AddressResource address;
    private AddressTypeResource addressType;

    public OrganisationAddressResource(OrganisationResource organisation, AddressResource address, AddressTypeResource addressType) {
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

    public AddressTypeResource getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressTypeResource addressType) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrganisationAddressResource that = (OrganisationAddressResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(organisation, that.organisation)
                .append(address, that.address)
                .append(addressType, that.addressType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(organisation)
                .append(address)
                .append(addressType)
                .toHashCode();
    }
}
