package com.worth.ifs.user.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * User Profile Data Transfer Object
 */
public class UserProfileResource extends UserProfileBaseResource {

    private Long user;

    public UserProfileResource() {
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UserProfileResource that = (UserProfileResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(this.user, that.getUser())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(this.user)
                .toHashCode();
    }
}
