package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CompetitionReadyToOpenKeyAssessmentStatisticsResource {

    private int assessorsInvited;
    private int assessorsAccepted;

    public int getAssessorsInvited() {
        return assessorsInvited;
    }

    public void setAssessorsInvited(int assessorsInvited) {
        this.assessorsInvited = assessorsInvited;
    }

    public int getAssessorsAccepted() {
        return assessorsAccepted;
    }

    public void setAssessorsAccepted(int assessorsAccepted) {
        this.assessorsAccepted = assessorsAccepted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompetitionReadyToOpenKeyAssessmentStatisticsResource that =
                (CompetitionReadyToOpenKeyAssessmentStatisticsResource) o;

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
