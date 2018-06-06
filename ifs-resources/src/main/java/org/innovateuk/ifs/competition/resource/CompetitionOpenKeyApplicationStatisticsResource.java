package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CompetitionOpenKeyApplicationStatisticsResource {

    private int applicationsPerAssessor;
    private int applicationsStarted;
    private int applicationsPastHalf;
    private int applicationsSubmitted;

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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final CompetitionOpenKeyApplicationStatisticsResource that =
                (CompetitionOpenKeyApplicationStatisticsResource) o;

        return new EqualsBuilder()
                .append(applicationsPerAssessor, that.applicationsPerAssessor)
                .append(applicationsStarted, that.applicationsStarted)
                .append(applicationsPastHalf, that.applicationsPastHalf)
                .append(applicationsSubmitted, that.applicationsSubmitted)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationsPerAssessor)
                .append(applicationsStarted)
                .append(applicationsPastHalf)
                .append(applicationsSubmitted)
                .toHashCode();
    }
}
