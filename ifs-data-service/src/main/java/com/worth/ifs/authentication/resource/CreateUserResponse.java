package com.worth.ifs.authentication.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents the return result from creating a User
 */
public class CreateUserResponse {

    private String uniqueId;

    /**
     * For JSON marshalling
     */
    public CreateUserResponse() {

    }

    public CreateUserResponse(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CreateUserResponse that = (CreateUserResponse) o;

        return new EqualsBuilder()
                .append(uniqueId, that.uniqueId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(uniqueId)
                .toHashCode();
    }
}
