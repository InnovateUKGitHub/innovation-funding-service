package org.innovateuk.ifs.cofunder.resource;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class CofunderDashboardCompetitionResource {

    private long competitionId;
    private String competitionName;
    private ZonedDateTime cofunderDeadlineDate;
    private long pendingAssessments;
    private FundingType fundingType;
    private long submitted;
    private ZonedDateTime cofunderAcceptDate;

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

    public ZonedDateTime getCofunderDeadlineDate() {
        return cofunderDeadlineDate;
    }

    public void setCofunderDeadlineDate(ZonedDateTime cofunderDeadlineDate) {
        this.cofunderDeadlineDate = cofunderDeadlineDate;
    }

    public long getPendingAssessments() {
        return pendingAssessments;
    }

    public void setPendingAssessments(long pendingAssessments) {
        this.pendingAssessments = pendingAssessments;
    }

    public FundingType getFundingType() {
        return fundingType;
    }

    public void setFundingType(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    public long getSubmitted() {
        return submitted;
    }

    public void setSubmitted(long submitted) {
        this.submitted = submitted;
    }

    public ZonedDateTime getCofunderAcceptDate() {
        return cofunderAcceptDate;
    }

    public void setCofunderAcceptDate(ZonedDateTime cofunderAcceptDate) {
        this.cofunderAcceptDate = cofunderAcceptDate;
    }

    public long getDaysLeft() {
        return ChronoUnit.DAYS.between(ZonedDateTime.now(), cofunderDeadlineDate);
    }

    public boolean hasPendingAssessments() {
        return pendingAssessments != 0;
    }
}