package com.worth.ifs.user.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
                .append(getTitle(), that.getTitle())
                .append(getFirstName(), that.getFirstName())
                .append(getLastName(), that.getLastName())
                .append(getPhoneNumber(), that.getPhoneNumber())
                .append(getGender(), that.getGender())
                .append(getDisability(), that.getDisability())
                .append(getEthnicity(), that.getEthnicity())
                .append(getAddress(), that.getAddress())
                .append(getEmail(), that.getEmail())
                .append(this.user, that.getUser())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getTitle())
                .append(getFirstName())
                .append(getLastName())
                .append(getPhoneNumber())
                .append(getGender())
                .append(getDisability())
                .append(getEthnicity())
                .append(getAddress())
                .append(getEmail())
                .append(this.user)
                .toHashCode();
    }
}
