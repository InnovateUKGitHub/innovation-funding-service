package org.innovateuk.ifs.authentication.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a request to an Identity Provider to update an existing User's email record
 */
public class UpdateEmailResource {

    private String email;

    public UpdateEmailResource() {
    }

    public UpdateEmailResource(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UpdateEmailResource that = (UpdateEmailResource) o;

        return new EqualsBuilder()
                .append(email, that.email)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(email)
                .toHashCode();
    }
}
