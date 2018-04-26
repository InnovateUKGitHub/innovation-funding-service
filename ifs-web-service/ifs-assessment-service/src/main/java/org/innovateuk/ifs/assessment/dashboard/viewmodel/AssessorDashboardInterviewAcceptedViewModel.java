package org.innovateuk.ifs.assessment.dashboard.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AssessorDashboardInterviewAcceptedViewModel  extends AssessorDashboardInterviewViewModel{

    private long awaitingApplications;

    public AssessorDashboardInterviewAcceptedViewModel(String competitionName, long competitionId, long awaitingApplications) {
        super(competitionName, competitionId);
        this.awaitingApplications = awaitingApplications;
    }

    public long getAwaitingApplications() {
        return awaitingApplications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorDashboardInterviewAcceptedViewModel that = (AssessorDashboardInterviewAcceptedViewModel) o;

        return new EqualsBuilder()
                .append(awaitingApplications, that.awaitingApplications)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(awaitingApplications)
                .toHashCode();
    }
}