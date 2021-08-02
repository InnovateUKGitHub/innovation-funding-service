package org.innovateuk.ifs.competition.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;

public class CompetitionTermsViewModel {

    private final long competitionId;
    GrantTermsAndConditionsResource termsAndConditions;
    private String termsAndConditionsLabel;
    private String termsAndConditionsGuidance;

    public CompetitionTermsViewModel(long competitionId) {
        this.competitionId = competitionId;
    }

    public CompetitionTermsViewModel(long competitionId,
                                     GrantTermsAndConditionsResource termsAndConditions,
                                     String termsAndConditionsLabel,
                                     String termsAndConditionsGuidance) {
        this.competitionId = competitionId;
        this.termsAndConditions = termsAndConditions;
        this.termsAndConditionsLabel = termsAndConditionsLabel;
        this.termsAndConditionsGuidance = termsAndConditionsGuidance;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public GrantTermsAndConditionsResource getTermsAndConditions() {
        return termsAndConditions;
    }

    public String getTermsAndConditionsLabel() {
        return termsAndConditionsLabel;
    }

    public String getTermsAndConditionsGuidance() {
        return termsAndConditionsGuidance;
    }

    public boolean isProcurementThirdParty() {
        return termsAndConditions.isThirdPartyProcurement();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionTermsViewModel that = (CompetitionTermsViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(termsAndConditions, that.termsAndConditions)
                .append(termsAndConditionsLabel, that.termsAndConditionsLabel)
                .append(termsAndConditionsGuidance, that.termsAndConditionsGuidance)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(termsAndConditions)
                .append(termsAndConditionsLabel)
                .append(termsAndConditionsGuidance)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "CompetitionTermsViewModel{" +
                "competitionId=" + competitionId +
                ",termsAndConditions=" + (termsAndConditions == null ? null : termsAndConditions.toString()) +
                ",termsAndConditionsLabel=" + termsAndConditionsLabel +
                ",termsAndConditionsGuidance=" + termsAndConditionsGuidance +
                '}';
    }
}