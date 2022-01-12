package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ExistingUserStagedInviteResource {

    private long userId;
    private long competitionId;

    public ExistingUserStagedInviteResource() {
    }

    public ExistingUserStagedInviteResource(long userId, long competitionId) {
        this.userId = userId;
        this.competitionId = competitionId;
    }

    public long getUserId() {
        return userId;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ExistingUserStagedInviteResource that = (ExistingUserStagedInviteResource) o;

        return new EqualsBuilder()
                .append(userId, that.userId)
                .append(competitionId, that.competitionId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userId)
                .append(competitionId)
                .toHashCode();
    }
}
