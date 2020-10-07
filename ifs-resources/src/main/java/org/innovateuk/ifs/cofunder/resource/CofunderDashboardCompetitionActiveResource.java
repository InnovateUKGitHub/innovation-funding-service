package org.innovateuk.ifs.cofunder.resource;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;

import java.time.ZonedDateTime;

public class CofunderDashboardCompetitionActiveResource {

    private long competitionId;
    private String competitionName;
    private ZonedDateTime cofunderDeadlineDate;
    private long pendingAssessments;
    private FundingType fundingType;
    private long daysLeft;

    public CofunderDashboardCompetitionActiveResource() {
    }

    public CofunderDashboardCompetitionActiveResource(long competitionId,
                                                      String competitionName,
                                                      ZonedDateTime cofunderDeadlineDate,
                                                      long pendingAssessments,
                                                      FundingType fundingType,
                                                      long daysLeft) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.cofunderDeadlineDate = cofunderDeadlineDate;
        this.pendingAssessments = pendingAssessments;
        this.fundingType = fundingType;
        this.daysLeft = daysLeft;
    }

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

    public long getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(long daysLeft) {
        this.daysLeft = daysLeft;
    }

    public boolean hasPendingAssessments() {
        return pendingAssessments != 0;
    }
}
