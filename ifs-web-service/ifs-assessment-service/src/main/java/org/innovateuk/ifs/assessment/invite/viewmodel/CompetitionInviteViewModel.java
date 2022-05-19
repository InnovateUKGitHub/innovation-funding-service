package org.innovateuk.ifs.assessment.invite.viewmodel;

import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;

/**
 * ViewModel of a CompetitionInvite.
 */
@Getter
public class CompetitionInviteViewModel extends BaseInviteViewModel {

    private ZonedDateTime acceptsDate;
    private ZonedDateTime deadlineDate;
    private ZonedDateTime briefingDate;
    private BigDecimal assessorPay;
    private FundingType competitionFundingType;
    private Boolean competitionAlwaysOpen;
    private String hash;

    public CompetitionInviteViewModel(String competitionInviteHash, CompetitionInviteResource invite, boolean userLoggedIn, String hash) {
        super(competitionInviteHash, invite.getCompetitionId(), invite.getCompetitionName(), userLoggedIn, hash);
        this.acceptsDate = invite.getAcceptsDate();
        this.deadlineDate = invite.getDeadlineDate();
        this.briefingDate = invite.getBriefingDate();
        this.assessorPay = invite.getAssessorPay();
        this.competitionFundingType = invite.getCompetitionFundingType();
        this.competitionAlwaysOpen = invite.getCompetitionAlwaysOpen();
        this.hash = hash;
    }

    public String getCompetitionInviteHash() {
        return getInviteHash();
    }

    public Boolean isKtpCompetition() {
        return KTP.equals(competitionFundingType);
    }

    public boolean isAlwaysOpenCompetition() {
        return BooleanUtils.isTrue(competitionAlwaysOpen);
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
