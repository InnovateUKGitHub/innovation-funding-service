package com.worth.ifs.assessment.viewmodel.registration;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the Become An Assessor view.
 */
public class AssessorRegistrationBecomeAnAssessorViewModel {

    private String competitionInviteHash;

    public AssessorRegistrationBecomeAnAssessorViewModel(String competitionInviteHash) {
        this.competitionInviteHash = competitionInviteHash;
    }

    public String getCompetitionInviteHash() {
        return competitionInviteHash;
    }

    public void setCompetitionInviteHash(String competitionInviteHash) {
        this.competitionInviteHash = competitionInviteHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorRegistrationBecomeAnAssessorViewModel that = (AssessorRegistrationBecomeAnAssessorViewModel) o;

        return new EqualsBuilder()
                .append(competitionInviteHash, that.competitionInviteHash)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionInviteHash)
                .toHashCode();
    }
}