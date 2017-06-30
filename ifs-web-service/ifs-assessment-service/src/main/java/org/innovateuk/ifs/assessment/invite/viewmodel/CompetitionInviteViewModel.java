package org.innovateuk.ifs.assessment.invite.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * ViewModel of a CompetitionInvite.
 */
public class CompetitionInviteViewModel {

    private String competitionInviteHash;
    private Long competitionId;
    private String competitionName;
    private ZonedDateTime acceptsDate;
    private ZonedDateTime deadlineDate;
    private ZonedDateTime briefingDate;
    private BigDecimal assessorPay;
    private boolean userLoggedIn;

    public CompetitionInviteViewModel(String competitionInviteHash, CompetitionInviteResource invite, boolean userLoggedIn) {
        this.competitionInviteHash = competitionInviteHash;
        this.competitionId = invite.getCompetitionId();
        this.competitionName = invite.getCompetitionName();
        this.acceptsDate = invite.getAcceptsDate();
        this.deadlineDate = invite.getDeadlineDate();
        this.briefingDate = invite.getBriefingDate();
        this.assessorPay = invite.getAssessorPay();
        this.userLoggedIn = userLoggedIn;
    }

    public String getCompetitionInviteHash() {
        return competitionInviteHash;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public ZonedDateTime getAcceptsDate() {
        return acceptsDate;
    }

    public ZonedDateTime getDeadlineDate() {
        return deadlineDate;
    }

    public ZonedDateTime getBriefingDate() {
        return briefingDate;
    }

    public BigDecimal getAssessorPay() {
        return assessorPay;
    }

    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionInviteViewModel that = (CompetitionInviteViewModel) o;

        return new EqualsBuilder()
                .append(userLoggedIn, that.userLoggedIn)
                .append(competitionInviteHash, that.competitionInviteHash)
                .append(competitionId, that.competitionId)
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
                .append(competitionId)
                .append(competitionName)
                .append(acceptsDate)
                .append(deadlineDate)
                .append(briefingDate)
                .append(assessorPay)
                .append(userLoggedIn)
                .toHashCode();
    }
}
