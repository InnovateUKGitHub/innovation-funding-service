package org.innovateuk.ifs.assessment.invite.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * ViewModel of a CompetitionInvite.
 */
public class CompetitionInviteViewModel extends BaseInviteViewModel {

    private ZonedDateTime acceptsDate;
    private ZonedDateTime deadlineDate;
    private ZonedDateTime briefingDate;
    private BigDecimal assessorPay;

    public CompetitionInviteViewModel(String competitionInviteHash, CompetitionInviteResource invite, boolean userLoggedIn) {
        super(competitionInviteHash, invite.getCompetitionId(), invite.getCompetitionName(), userLoggedIn);
        this.acceptsDate = invite.getAcceptsDate();
        this.deadlineDate = invite.getDeadlineDate();
        this.briefingDate = invite.getBriefingDate();
        this.assessorPay = invite.getAssessorPay();
    }

    public String getCompetitionInviteHash() {
        return getInviteHash();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionInviteViewModel that = (CompetitionInviteViewModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(acceptsDate, that.acceptsDate)
                .append(deadlineDate, that.deadlineDate)
                .append(briefingDate, that.briefingDate)
                .append(assessorPay, that.assessorPay)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(acceptsDate)
                .append(deadlineDate)
                .append(briefingDate)
                .append(assessorPay)
                .toHashCode();
    }
}
