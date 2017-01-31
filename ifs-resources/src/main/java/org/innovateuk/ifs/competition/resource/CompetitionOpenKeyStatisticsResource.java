package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CompetitionOpenKeyStatisticsResource {
    private long assessorsInvited;
    private long assessorsAccepted;
    private long applicationsPerAssessor;
    private long applicationsStarted;
    private long applicationsPastHalf;
    private long applicationsSubmitted;

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

    public long getApplicationsPerAssessor() {
        return applicationsPerAssessor;
    }

    public void setApplicationsPerAssessor(long applicationsPerAssessor) {
        this.applicationsPerAssessor = applicationsPerAssessor;
    }

    public long getApplicationsStarted() {
        return applicationsStarted;
    }

    public void setApplicationsStarted(long applicationsStarted) {
        this.applicationsStarted = applicationsStarted;
    }

    public long getApplicationsPastHalf() {
        return applicationsPastHalf;
    }

    public void setApplicationsPastHalf(long applicationsPastHalf) {
        this.applicationsPastHalf = applicationsPastHalf;
    }

    public long getApplicationsSubmitted() {
        return applicationsSubmitted;
    }

    public void setApplicationsSubmitted(long applicationsSubmitted) {
        this.applicationsSubmitted = applicationsSubmitted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionOpenKeyStatisticsResource that = (CompetitionOpenKeyStatisticsResource) o;

        return new EqualsBuilder()
                .append(assessorsInvited, that.assessorsInvited)
                .append(assessorsAccepted, that.assessorsAccepted)
                .append(applicationsPerAssessor, that.applicationsPerAssessor)
                .append(applicationsStarted, that.applicationsStarted)
                .append(applicationsPastHalf, that.applicationsPastHalf)
                .append(applicationsSubmitted, that.applicationsSubmitted)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessorsInvited)
                .append(assessorsAccepted)
                .append(applicationsPerAssessor)
                .append(applicationsStarted)
                .append(applicationsPastHalf)
                .append(applicationsSubmitted)
                .toHashCode();
    }
}
