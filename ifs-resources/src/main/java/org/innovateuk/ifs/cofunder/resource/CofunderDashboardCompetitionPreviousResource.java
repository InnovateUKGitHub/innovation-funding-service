package org.innovateuk.ifs.cofunder.resource;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;

public class CofunderDashboardCompetitionPreviousResource {

    private long competitionId;
    private String competitionName;
    private long reviewed;
    private FundingType fundingType;

    public CofunderDashboardCompetitionPreviousResource() {
    }

    public CofunderDashboardCompetitionPreviousResource(long competitionId,
                                                        String competitionName,
//                                                        long reviewed,
                                                        FundingType fundingType) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
//        this.reviewed = reviewed;
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

    public long getReviewed() {
        return reviewed;
    }

    public void setReviewed(long reviewed) {
        this.reviewed = reviewed;
    }

    public FundingType getFundingType() {
        return fundingType;
    }

    public void setFundingType(FundingType fundingType) {
        this.fundingType = fundingType;
    }
}
