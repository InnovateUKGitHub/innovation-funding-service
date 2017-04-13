package org.innovateuk.ifs.assessment.profile.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.UserProfileStatusResource;

/**
 * Model attributes for an Assessor's Profile status.
 */
public class AssessorProfileStatusViewModel {
    private final boolean skillsComplete;
    private final boolean affiliationsComplete;
    private final boolean agreementComplete;

    public AssessorProfileStatusViewModel(UserProfileStatusResource userProfileStatusResource) {
        this(userProfileStatusResource.isSkillsComplete(),
                userProfileStatusResource.isAffiliationsComplete(),
                userProfileStatusResource.isAgreementComplete()
        );
    }

    private AssessorProfileStatusViewModel(boolean skillsComplete, boolean affiliationsComplete, boolean agreementComplete) {
        this.skillsComplete = skillsComplete;
        this.affiliationsComplete = affiliationsComplete;
        this.agreementComplete = agreementComplete;
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

    public boolean isComplete() {
        return skillsComplete && affiliationsComplete && agreementComplete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorProfileStatusViewModel that = (AssessorProfileStatusViewModel) o;

        return new EqualsBuilder()
                .append(skillsComplete, that.skillsComplete)
                .append(affiliationsComplete, that.affiliationsComplete)
                .append(agreementComplete, that.agreementComplete)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(skillsComplete)
                .append(affiliationsComplete)
                .append(agreementComplete)
                .toHashCode();
    }
}
