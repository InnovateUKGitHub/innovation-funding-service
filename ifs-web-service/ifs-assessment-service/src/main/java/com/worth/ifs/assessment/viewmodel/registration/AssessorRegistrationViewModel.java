package com.worth.ifs.assessment.viewmodel.registration;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the Assessor Registration.
 */
public class AssessorRegistrationViewModel {
    private String competitionInviteHash;
    private String email;

    public AssessorRegistrationViewModel(String competitionInviteHash, String email) {
        this.email = email;
        this.competitionInviteHash = competitionInviteHash;
    }

    public String getCompetitionInviteHash() {
        return competitionInviteHash;
    }

    public void setCompetitionInviteHash(String competitionInviteHash) {
        this.competitionInviteHash = competitionInviteHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorRegistrationViewModel that = (AssessorRegistrationViewModel) o;

        return new EqualsBuilder()
                .append(competitionInviteHash, that.competitionInviteHash)
                .append(email, that.email)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionInviteHash)
                .append(email)
                .toHashCode();
    }
}
