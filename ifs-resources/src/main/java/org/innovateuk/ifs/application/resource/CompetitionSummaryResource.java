package org.innovateuk.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a high-level overview of an application.
 */
public class CompetitionSummaryResource {

    private long competitionId;
    private String competitionName;
    private CompetitionStatus competitionStatus;
    private LocalDateTime applicationDeadline;
    private int totalNumberOfApplications;
    private int applicationsStarted;
    private int applicationsInProgress;
    private int applicationsSubmitted;
    private int applicationsNotSubmitted;
    private int applicationsFunded;
    private int ineligibleApplications;
    private int assessorsInvited;

    public long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(long competitionId) {
        this.competitionId = competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public LocalDateTime getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDateTime applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public String getFormattedApplicationDeadline() {
        if (applicationDeadline == null) {
            return "";
        }
        return applicationDeadline.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"));
    }

    public int getTotalNumberOfApplications() {
        return totalNumberOfApplications;
    }

    public void setTotalNumberOfApplications(int totalNumberOfApplications) {
        this.totalNumberOfApplications = totalNumberOfApplications;
    }

    public int getApplicationsStarted() {
        return applicationsStarted;
    }

    public void setApplicationsStarted(int applicationsStarted) {
        this.applicationsStarted = applicationsStarted;
    }

    public int getApplicationsInProgress() {
        return applicationsInProgress;
    }

    public void setApplicationsInProgress(int applicationsInProgress) {
        this.applicationsInProgress = applicationsInProgress;
    }

    public int getApplicationsSubmitted() {
        return applicationsSubmitted;
    }

    public void setApplicationsSubmitted(int applicationsSubmitted) {
        this.applicationsSubmitted = applicationsSubmitted;
    }

    public int getApplicationsNotSubmitted() {
        return applicationsNotSubmitted;
    }

    public void setApplicationsNotSubmitted(int applicationsNotSubmitted) {
        this.applicationsNotSubmitted = applicationsNotSubmitted;
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }

    public void setCompetitionStatus(CompetitionStatus competitionStatus) {
        this.competitionStatus = competitionStatus;
    }

    public int getApplicationsFunded() {
        return applicationsFunded;
    }

    public void setApplicationsFunded(int applicationsFunded) {
        this.applicationsFunded = applicationsFunded;
    }

    public int getIneligibleApplications() {
        return ineligibleApplications;
    }

    public void setIneligibleApplications(int ineligibleApplications) {
        this.ineligibleApplications = ineligibleApplications;
    }

    public int getAssessorsInvited() {
        return assessorsInvited;
    }

    public void setAssessorsInvited(int assessorsInvited) {
        this.assessorsInvited = assessorsInvited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionSummaryResource that = (CompetitionSummaryResource) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(competitionStatus, that.competitionStatus)
                .append(applicationDeadline, that.applicationDeadline)
                .append(totalNumberOfApplications, that.totalNumberOfApplications)
                .append(applicationsStarted, that.applicationsStarted)
                .append(applicationsInProgress, that.applicationsInProgress)
                .append(applicationsSubmitted, that.applicationsSubmitted)
                .append(applicationsNotSubmitted, that.applicationsNotSubmitted)
                .append(applicationsFunded, that.applicationsFunded)
                .append(ineligibleApplications, that.ineligibleApplications)
                .append(assessorsInvited, that.assessorsInvited)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .toHashCode();
    }
}
