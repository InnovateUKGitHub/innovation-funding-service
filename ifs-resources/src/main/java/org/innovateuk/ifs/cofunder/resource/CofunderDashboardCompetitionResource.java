package org.innovateuk.ifs.supporter.resource;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class SupporterDashboardCompetitionResource {

    private long competitionId;
    private String competitionName;
    private ZonedDateTime supporterDeadlineDate;
    private long pendingAssessments;
    private FundingType fundingType;
    private long submitted;
    private ZonedDateTime supporterAcceptDate;

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

    public ZonedDateTime getSupporterDeadlineDate() {
        return supporterDeadlineDate;
    }

    public void setSupporterDeadlineDate(ZonedDateTime supporterDeadlineDate) {
        this.supporterDeadlineDate = supporterDeadlineDate;
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

    public ZonedDateTime getSupporterAcceptDate() {
        return supporterAcceptDate;
    }

    public void setSupporterAcceptDate(ZonedDateTime supporterAcceptDate) {
        this.supporterAcceptDate = supporterAcceptDate;
    }

    public long getDaysLeft() {
        return ChronoUnit.DAYS.between(ZonedDateTime.now(), supporterDeadlineDate);
    }

    public boolean hasPendingAssessments() {
        return pendingAssessments != 0;
    }
}