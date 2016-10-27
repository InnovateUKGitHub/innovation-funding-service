package com.worth.ifs.user.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by tom on 26/10/2016.
 */
public class UserProfileStatusResource {

    private boolean skillsComplete;
    private boolean affiliationsComplete;
    private boolean contractComplete;

    UserProfileStatusResource() {
        // default constructor
    }

    public UserProfileStatusResource(boolean skillsComplete, boolean affiliationsComplete, boolean contractComplete) {
        this.skillsComplete = skillsComplete;
        this.affiliationsComplete = affiliationsComplete;
        this.contractComplete = contractComplete;
    }

    public boolean isSkillsComplete() {
        return skillsComplete;
    }

    public boolean isAffiliationsComplete() {
        return affiliationsComplete;
    }

    public boolean isContractComplete() {
        return contractComplete;
    }

    public void setSkillsComplete(boolean skillsComplete) {
        this.skillsComplete = skillsComplete;
    }

    public void setAffiliationsComplete(boolean affiliationsComplete) {
        this.affiliationsComplete = affiliationsComplete;
    }

    public void setContractComplete(boolean contractComplete) {
        this.contractComplete = contractComplete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UserProfileStatusResource that = (UserProfileStatusResource) o;

        return new EqualsBuilder()
                .append(skillsComplete, that.skillsComplete)
                .append(affiliationsComplete, that.affiliationsComplete)
                .append(contractComplete, that.contractComplete)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(skillsComplete)
                .append(affiliationsComplete)
                .append(contractComplete)
                .toHashCode();
    }
}
