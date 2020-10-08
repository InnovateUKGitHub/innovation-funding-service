package org.innovateuk.ifs.cofunder.domain;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.time.ZonedDateTime;

public class CompetitionForCofunding {
    private long competitionId;
    private String competitionName;
    private FundingType fundingType;
    private ZonedDateTime cofunderDeadline;
    private ZonedDateTime cofunderAcceptDate;
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
        this.cofunderDeadline = competition.getAssessorDeadlineDate();
        this.cofunderAcceptDate = competition.getAssessorAcceptsDate();
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

    public ZonedDateTime getCofunderDeadline() {
        return cofunderDeadline;
    }

    public void setCofunderDeadline(ZonedDateTime cofunderDeadline) {
        this.cofunderDeadline = cofunderDeadline;
    }

    public ZonedDateTime getCofunderAcceptDate() {
        return cofunderAcceptDate;
    }

    public void setCofunderAcceptDate(ZonedDateTime cofunderAcceptDate) {
        this.cofunderAcceptDate = cofunderAcceptDate;
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
