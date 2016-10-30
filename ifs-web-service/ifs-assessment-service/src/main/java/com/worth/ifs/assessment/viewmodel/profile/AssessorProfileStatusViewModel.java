package com.worth.ifs.assessment.viewmodel.profile;

import com.worth.ifs.user.resource.UserProfileStatusResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by tom on 26/10/2016.
 */
public class AssessorProfileStatusViewModel {
    private final boolean skillsComplete;
    private final boolean affiliationsComplete;
    private final boolean contractComplete;

    public AssessorProfileStatusViewModel(UserProfileStatusResource userProfileStatusResource) {
        this(   userProfileStatusResource.isSkillsComplete(),
                userProfileStatusResource.isAffiliationsComplete(),
                userProfileStatusResource.isContractComplete()
        );
    }

    private AssessorProfileStatusViewModel(boolean skillsComplete, boolean affiliationsComplete, boolean contractComplete) {
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

    public boolean isComplete() {
        return skillsComplete && affiliationsComplete && contractComplete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorProfileStatusViewModel that = (AssessorProfileStatusViewModel) o;

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
