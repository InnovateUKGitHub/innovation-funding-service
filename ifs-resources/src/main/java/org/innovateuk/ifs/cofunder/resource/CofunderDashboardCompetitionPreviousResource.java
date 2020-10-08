package org.innovateuk.ifs.cofunder.resource;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;

public class CofunderDashboardCompetitionPreviousResource {

    private long competitionId;
    private String competitionName;
    private long submitted;
    private FundingType fundingType;

    public CofunderDashboardCompetitionPreviousResource() {
    }

    public CofunderDashboardCompetitionPreviousResource(long competitionId,
                                                        String competitionName,
                                                        long submitted,
                                                        FundingType fundingType) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.submitted = submitted;
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

    public long getSubmitted() {
        return submitted;
    }

    public void setSubmitted(long submitted) {
        this.submitted = submitted;
    }

    public FundingType getFundingType() {
        return fundingType;
    }

    public void setFundingType(FundingType fundingType) {
        this.fundingType = fundingType;
    }
}
