package org.innovateuk.ifs.assessment.dashboard.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;

public class AssessorDashboardInterviewAcceptedViewModel extends AssessorDashboardInterviewViewModel{

    private LocalDate interviewDateDeadline;
    private long daysLeft;
    private long awaitingApplications;

    public AssessorDashboardInterviewAcceptedViewModel(String competitionName, long competitionId, LocalDate interviewDateDeadline, long daysLeft, long awaitingApplications) {
        super(competitionName, competitionId);
        this.interviewDateDeadline = interviewDateDeadline;
        this.daysLeft = daysLeft;
        this.awaitingApplications = awaitingApplications;
    }

    public LocalDate getInterviewDateDeadline() {
        return interviewDateDeadline;
    }

    public long getDaysLeft() {
        return daysLeft;
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
                .append(daysLeft, that.daysLeft)
                .append(awaitingApplications, that.awaitingApplications)
                .append(interviewDateDeadline, that.interviewDateDeadline)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(interviewDateDeadline)
                .append(daysLeft)
                .append(awaitingApplications)
                .toHashCode();
    }
}
