package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.invite.resource.CompetitionInviteResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ViewModel of a CompetitionInvite.
 */
public class CompetitionInviteViewModel {

    private String competitionInviteHash;
    private String competitionName;
    private LocalDateTime acceptsDate;
    private LocalDateTime deadlineDate;
    private LocalDateTime briefingDate;
    private BigDecimal assessorPay;

    public CompetitionInviteViewModel(String competitionInviteHash, CompetitionInviteResource invite) {
        this.competitionInviteHash = competitionInviteHash;
        this.competitionName = invite.getCompetitionName();
        this.acceptsDate = invite.getAcceptsDate();
        this.deadlineDate = invite.getDeadlineDate();
        this.briefingDate = invite.getBriefingDate();
        this.assessorPay = invite.getAssessorPay();
    }

    public String getCompetitionInviteHash() {
        return competitionInviteHash;
    }

    public void setCompetitionInviteHash(String competitionInviteHash) {
        this.competitionInviteHash = competitionInviteHash;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public LocalDateTime getAcceptsDate() {
        return acceptsDate;
    }

    public void setAcceptsDate(LocalDateTime acceptsDate) {
        this.acceptsDate = acceptsDate;
    }

    public LocalDateTime getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(LocalDateTime deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public LocalDateTime getBriefingDate() {
        return briefingDate;
    }

    public void setBriefingDate(LocalDateTime briefingDate) {
        this.briefingDate = briefingDate;
    }

    public BigDecimal getAssessorPay() {
        return assessorPay;
    }

    public void setAssessorPay(BigDecimal assessorPay) {
        this.assessorPay = assessorPay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompetitionInviteViewModel that = (CompetitionInviteViewModel) o;

        return new EqualsBuilder()
                .append(competitionInviteHash, that.competitionInviteHash)
                .append(competitionName, that.competitionName)
                .append(acceptsDate, that.acceptsDate)
                .append(deadlineDate, that.deadlineDate)
                .append(briefingDate, that.briefingDate)
                .append(assessorPay, that.assessorPay)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionInviteHash)
                .append(competitionName)
                .append(acceptsDate)
                .append(deadlineDate)
                .append(briefingDate)
                .append(assessorPay)
                .toHashCode();
    }
}
