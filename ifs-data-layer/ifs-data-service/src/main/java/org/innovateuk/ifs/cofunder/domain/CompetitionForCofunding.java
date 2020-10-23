package org.innovateuk.ifs.supporter.domain;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.time.ZonedDateTime;

/**
 * This allows us to use competition as a constructor argument (enabling us to use its getCompetitionStatus method)
 * as the resource DTO layer does not have access to domain classes.
 */
public class CompetitionForCofunding {
    private long competitionId;
    private String competitionName;
    private FundingType fundingType;
    private ZonedDateTime supporterDeadline;
    private ZonedDateTime supporterAcceptDate;
    private long assigned;
    private long rejected;
    private long accepted;
    private CompetitionStatus competitionStatus;

    public CompetitionForCofunding() {
    }

    public CompetitionForCofunding(Competition competition,
                                   long assigned,
                                   long rejected,
                                   long accepted) {
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.fundingType = competition.getFundingType();
        this.supporterDeadline = competition.getAssessorDeadlineDate();
        this.supporterAcceptDate = competition.getAssessorAcceptsDate();
        this.assigned = assigned;
        this.rejected = rejected;
        this.accepted = accepted;
        this.competitionStatus = competition.getCompetitionStatus();
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

    public FundingType getFundingType() {
        return fundingType;
    }

    public void setFundingType(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    public ZonedDateTime getSupporterDeadline() {
        return supporterDeadline;
    }

    public void setSupporterDeadline(ZonedDateTime supporterDeadline) {
        this.supporterDeadline = supporterDeadline;
    }

    public ZonedDateTime getSupporterAcceptDate() {
        return supporterAcceptDate;
    }

    public void setSupporterAcceptDate(ZonedDateTime supporterAcceptDate) {
        this.supporterAcceptDate = supporterAcceptDate;
    }

    public long getAssigned() {
        return assigned;
    }

    public void setAssigned(long assigned) {
        this.assigned = assigned;
    }

    public long getRejected() {
        return rejected;
    }

    public void setRejected(long rejected) {
        this.rejected = rejected;
    }

    public long getAccepted() {
        return accepted;
    }

    public void setAccepted(long accepted) {
        this.accepted = accepted;
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }

    public void setCompetitionStatus(CompetitionStatus competitionStatus) {
        this.competitionStatus = competitionStatus;
    }
}
