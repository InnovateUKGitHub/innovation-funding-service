package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CompetitionReadyToOpenKeyStatisticsResource {
    private long assessorsInvited;
    private long assessorsAccepted;

    public long getAssessorsInvited() {
        return assessorsInvited;
    }

    public void setAssessorsInvited(long assessorsInvited) {
        this.assessorsInvited = assessorsInvited;
    }

    public long getAssessorsAccepted() {
        return assessorsAccepted;
    }

    public void setAssessorsAccepted(long assessorsAccepted) {
        this.assessorsAccepted = assessorsAccepted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionReadyToOpenKeyStatisticsResource that = (CompetitionReadyToOpenKeyStatisticsResource) o;

        return new EqualsBuilder()
                .append(assessorsInvited, that.assessorsInvited)
                .append(assessorsAccepted, that.assessorsAccepted)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessorsInvited)
                .append(assessorsAccepted)
                .toHashCode();
    }
}
