package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CompetitionClosedKeyAssessmentStatisticsResource {

    private int assessorsInvited;
    private int assessorsAccepted;
    private int assessorsWithoutApplications;

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

    public int getAssessorsWithoutApplications() {
        return assessorsWithoutApplications;
    }

    public void setAssessorsWithoutApplications(int assessorsWithoutApplications) {
        this.assessorsWithoutApplications = assessorsWithoutApplications;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final CompetitionClosedKeyAssessmentStatisticsResource that =
                (CompetitionClosedKeyAssessmentStatisticsResource) o;

        return new EqualsBuilder()
                .append(assessorsInvited, that.assessorsInvited)
                .append(assessorsAccepted, that.assessorsAccepted)
                .append(assessorsWithoutApplications, that.assessorsWithoutApplications)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessorsInvited)
                .append(assessorsAccepted)
                .append(assessorsWithoutApplications)
                .toHashCode();
    }
}
