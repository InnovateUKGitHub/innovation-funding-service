package org.innovateuk.ifs.cofunder.resource;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;

import java.time.ZonedDateTime;

public class CofunderDashboardCompetitionAwaitingResource {

    private long competitionId;
    private String competitionName;
    private ZonedDateTime cofunderDeadlineDate;
    private long awaitingReview;
    private FundingType fundingType;


    public CofunderDashboardCompetitionAwaitingResource() {
    }

    public CofunderDashboardCompetitionAwaitingResource(long competitionId,
                                                        String competitionName,
                                                        ZonedDateTime cofunderDeadlineDate,
                                                        long awaitingReview,
                                                        FundingType fundingType) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.cofunderDeadlineDate = cofunderDeadlineDate;
        this.awaitingReview = awaitingReview;
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

    public ZonedDateTime getCofunderDeadlineDate() {
        return cofunderDeadlineDate;
    }

    public void setCofunderDeadlineDate(ZonedDateTime cofunderDeadlineDate) {
        this.cofunderDeadlineDate = cofunderDeadlineDate;
    }

    public long getAwaitingReview() {
        return awaitingReview;
    }

    public void setAwaitingReview(long awaitingReview) {
        this.awaitingReview = awaitingReview;
    }

    public FundingType getFundingType() {
        return fundingType;
    }

    public void setFundingType(FundingType fundingType) {
        this.fundingType = fundingType;
    }
}
