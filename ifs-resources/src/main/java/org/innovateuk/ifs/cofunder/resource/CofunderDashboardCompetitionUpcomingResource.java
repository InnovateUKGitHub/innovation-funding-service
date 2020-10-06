package org.innovateuk.ifs.cofunder.resource;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;

import java.time.ZonedDateTime;

public class CofunderDashboardCompetitionUpcomingResource {

    private long competitionId;
    private String competitionName;
    private ZonedDateTime cofunderAcceptDate;
    private ZonedDateTime cofunderDeadlineDate;
    private long upcomingReview;
    private FundingType fundingType;

    public CofunderDashboardCompetitionUpcomingResource() {
    }

    public CofunderDashboardCompetitionUpcomingResource(long competitionId,
                                                        String competitionName,
                                                        ZonedDateTime cofunderAcceptDate,
                                                        ZonedDateTime cofunderDeadlineDate,
                                                        long upcomingReview,
                                                        FundingType fundingType) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.cofunderAcceptDate = cofunderAcceptDate;
        this.cofunderDeadlineDate = cofunderDeadlineDate;
        this.upcomingReview = upcomingReview;
        this.fundingType = fundingType;
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

    public ZonedDateTime getCofunderAcceptDate() {
        return cofunderAcceptDate;
    }

    public void setCofunderAcceptDate(ZonedDateTime cofunderAcceptDate) {
        this.cofunderAcceptDate = cofunderAcceptDate;
    }

    public ZonedDateTime getCofunderDeadlineDate() {
        return cofunderDeadlineDate;
    }

    public void setCofunderDeadlineDate(ZonedDateTime cofunderDeadlineDate) {
        this.cofunderDeadlineDate = cofunderDeadlineDate;
    }

    public long getUpcomingReview() {
        return upcomingReview;
    }

    public void setUpcomingReview(long upcomingReview) {
        this.upcomingReview = upcomingReview;
    }

    public FundingType getFundingType() {
        return fundingType;
    }

    public void setFundingType(FundingType fundingType) {
        this.fundingType = fundingType;
    }
}
