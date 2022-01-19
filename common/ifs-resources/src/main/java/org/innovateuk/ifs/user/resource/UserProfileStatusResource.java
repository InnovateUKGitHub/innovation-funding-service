package org.innovateuk.ifs.user.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO representing the Status of a User's Profile.
 */
public class UserProfileStatusResource {

    private long user; // added in so we can do a security check when fetching profile status
    private boolean skillsComplete;
    private boolean affiliationsComplete;
    private boolean agreementComplete;

    UserProfileStatusResource() {
    }

    public UserProfileStatusResource(long user, boolean skillsComplete, boolean affiliationsComplete, boolean agreementComplete) {
        this.user = user;
        this.skillsComplete = skillsComplete;
        this.affiliationsComplete = affiliationsComplete;
        this.agreementComplete = agreementComplete;
    }

    public long getUser() {
        return user;
    }

    public boolean isSkillsComplete() {
        return skillsComplete;
    }

    public boolean isAffiliationsComplete() {
        return affiliationsComplete;
    }

    public boolean isAgreementComplete() {
        return agreementComplete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserProfileStatusResource that = (UserProfileStatusResource) o;

        return new EqualsBuilder()
                .append(user, that.user)
                .append(skillsComplete, that.skillsComplete)
                .append(affiliationsComplete, that.affiliationsComplete)
                .append(agreementComplete, that.agreementComplete)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(user)
                .append(skillsComplete)
                .append(affiliationsComplete)
                .append(agreementComplete)
                .toHashCode();
    }
}
