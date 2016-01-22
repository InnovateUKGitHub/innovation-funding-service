package com.worth.ifs.authentication.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a request to an Identity Provider to update an existing User record
 */
public class UpdateUserResource {

    private String password;

    /**
     * For JSON marshalling
     */
    public UpdateUserResource() {
    }

    public UpdateUserResource(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UpdateUserResource that = (UpdateUserResource) o;

        return new EqualsBuilder()
                .append(password, that.password)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(password)
                .toHashCode();
    }
}
