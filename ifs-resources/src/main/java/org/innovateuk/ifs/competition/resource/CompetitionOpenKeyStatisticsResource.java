package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CompetitionOpenKeyStatisticsResource {
    private int assessorsInvited;
    private int assessorsAccepted;
    private int applicationsPerAssessor;
    private int applicationsStarted;
    private int applicationsPastHalf;
    private int applicationsSubmitted;

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

    public int getApplicationsPerAssessor() {
        return applicationsPerAssessor;
    }

    public void setApplicationsPerAssessor(int applicationsPerAssessor) {
        this.applicationsPerAssessor = applicationsPerAssessor;
    }

    public int getApplicationsStarted() {
        return applicationsStarted;
    }

    public void setApplicationsStarted(int applicationsStarted) {
        this.applicationsStarted = applicationsStarted;
    }

    public int getApplicationsPastHalf() {
        return applicationsPastHalf;
    }

    public void setApplicationsPastHalf(int applicationsPastHalf) {
        this.applicationsPastHalf = applicationsPastHalf;
    }

    public int getApplicationsSubmitted() {
        return applicationsSubmitted;
    }

    public void setApplicationsSubmitted(int applicationsSubmitted) {
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
