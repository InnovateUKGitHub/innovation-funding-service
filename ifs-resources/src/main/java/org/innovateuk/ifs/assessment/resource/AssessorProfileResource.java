package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.UserResource;


/**
 * DTO to encapsulate an Assessors profile view.
 */
public class AssessorProfileResource {

    private UserResource user;
    private ProfileResource profile;

    public AssessorProfileResource() {
    }

    public AssessorProfileResource(UserResource user, ProfileResource profile) {
        this.user = user;
        this.profile = profile;
    }

    public UserResource getUser() {
        return user;
    }

    public ProfileResource getProfile() {
        return profile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorProfileResource that = (AssessorProfileResource) o;

        return new EqualsBuilder()
                .append(user, that.user)
                .append(profile, that.profile)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(user)
                .append(profile)
                .toHashCode();
    }
}
