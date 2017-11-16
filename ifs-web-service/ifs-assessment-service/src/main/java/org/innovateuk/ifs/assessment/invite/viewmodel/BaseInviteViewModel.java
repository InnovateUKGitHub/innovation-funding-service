package org.innovateuk.ifs.assessment.invite.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class BaseInviteViewModel {
    private String inviteHash;
    private Long competitionId;
    private String competitionName;
    private boolean userLoggedIn;

    protected BaseInviteViewModel(String inviteHash, Long competitionId, String competitionName, boolean userLoggedIn) {
        this.inviteHash = inviteHash;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.userLoggedIn = userLoggedIn;
    }

    public String getInviteHash() {
        return inviteHash;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BaseInviteViewModel that = (BaseInviteViewModel) o;

        return new EqualsBuilder()
                .append(userLoggedIn, that.userLoggedIn)
                .append(inviteHash, that.inviteHash)
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(inviteHash)
                .append(competitionId)
                .append(competitionName)
                .append(userLoggedIn)
                .toHashCode();
    }
}
