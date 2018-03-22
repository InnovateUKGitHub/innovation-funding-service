package org.innovateuk.ifs.assessment.dashboard.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for interview panel invites on the assessor dashboard.
 */
public class AssessorDashboardInterviewInviteViewModel extends AssessorDashboardInterviewViewModel {

    private String inviteHash;

    public AssessorDashboardInterviewInviteViewModel(String competitionName, long competitionId, String hash) {
        super(competitionName, competitionId);
        this.inviteHash = hash;
    }

    public String getInviteHash() {
        return inviteHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorDashboardInterviewInviteViewModel that = (AssessorDashboardInterviewInviteViewModel) o;

        return new EqualsBuilder()
                .append(inviteHash, that.inviteHash)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(inviteHash)
                .toHashCode();
    }
}