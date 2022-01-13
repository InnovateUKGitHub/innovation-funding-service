package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class StagedApplicationResource {

    private long applicationId;
    private long competitionId;

    public StagedApplicationResource() {
    }

    public StagedApplicationResource(long applicationId, long competitionId) {
        this.applicationId = applicationId;
        this.competitionId = competitionId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        StagedApplicationResource that = (StagedApplicationResource) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(competitionId, that.competitionId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(competitionId)
                .toHashCode();
    }
}
