package org.innovateuk.ifs.assessment.profile.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.RoleProfileState;
import org.innovateuk.ifs.user.resource.UserProfileStatusResource;

import static org.innovateuk.ifs.user.resource.RoleProfileState.ACTIVE;
import static org.innovateuk.ifs.user.resource.RoleProfileState.UNAVAILABLE;

/**
 * Model attributes for an Assessor's Profile status.
 */
public class AssessorProfileStatusViewModel {

    static final String UNAVAILABLE_ASSESSOR_BANNER =  "Your assessor role is unavailable, so you cannot access any applications.";
    static final String DISABLED_ASSESSOR_BANNER =  "Your assessor role has been disabled. You can no longer access any applications.";

    private final boolean skillsComplete;
    private final boolean affiliationsComplete;
    private final boolean agreementComplete;
    private final RoleProfileState roleProfileState;

    public AssessorProfileStatusViewModel(UserProfileStatusResource userProfileStatusResource, RoleProfileState roleProfileState) {
        this.skillsComplete = userProfileStatusResource.isSkillsComplete();
        this.affiliationsComplete = userProfileStatusResource.isAffiliationsComplete();
        this.agreementComplete = userProfileStatusResource.isAgreementComplete();
        this.roleProfileState = roleProfileState;
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

    /**
     * View model logic
     */
    public boolean displayBannerMessage() {
        return !ACTIVE.equals(this.roleProfileState);
    }

    public String getBannerMessage() {
        if (UNAVAILABLE.equals(this.roleProfileState)) {
            return UNAVAILABLE_ASSESSOR_BANNER;
        } else {
            return DISABLED_ASSESSOR_BANNER;
        }
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
                .append(roleProfileState, that.roleProfileState)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(skillsComplete)
                .append(affiliationsComplete)
                .append(agreementComplete)
                .append(roleProfileState)
                .toHashCode();
    }
}