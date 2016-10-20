package com.worth.ifs.user.resource;

import com.worth.ifs.address.resource.AddressResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ProfileAddressResource {
    private Long user;
    private AddressResource address;

    public ProfileAddressResource() {
    }

    public ProfileAddressResource(Long user, AddressResource address) {
        this.user = user;
        this.address = address;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public AddressResource getAddress() {
        return address;
    }

    public void setAddress(AddressResource address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProfileAddressResource that = (ProfileAddressResource) o;

        return new EqualsBuilder()
                .append(user, that.user)
                .append(address, that.address)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(user)
                .append(address)
                .toHashCode();
    }
}
