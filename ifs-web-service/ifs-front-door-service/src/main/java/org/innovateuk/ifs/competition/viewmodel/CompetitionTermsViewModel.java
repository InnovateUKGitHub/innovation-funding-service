package org.innovateuk.ifs.competition.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;

public class CompetitionTermsViewModel {

    private final long competitionId;
    GrantTermsAndConditionsResource termsAndConditions;
    private FileEntryResource competitionTerms;
    private CompetitionThirdPartyConfigResource thirdPartyConfig;

    public CompetitionTermsViewModel(long competitionId) {
        this.competitionId = competitionId;
    }

    public CompetitionTermsViewModel(long competitionId,
                                     GrantTermsAndConditionsResource termsAndConditions) {
        this.competitionId = competitionId;
        this.termsAndConditions = termsAndConditions;
    }

    public CompetitionTermsViewModel(long competitionId,
                                     GrantTermsAndConditionsResource termsAndConditions,
                                     FileEntryResource competitionTerms,
                                     CompetitionThirdPartyConfigResource thirdPartyConfig) {
        this.competitionId = competitionId;
        this.termsAndConditions = termsAndConditions;
        this.competitionTerms = competitionTerms;
        this.thirdPartyConfig = thirdPartyConfig;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public GrantTermsAndConditionsResource getTermsAndConditions() {
        return termsAndConditions;
    }

    public CompetitionThirdPartyConfigResource getThirdPartyConfig() {
        return thirdPartyConfig;
    }

    public boolean isProcurementThirdParty() {
        return termsAndConditions.isProcurementThirdParty();
    }

    public boolean isTermsAndConditionsUploaded() {
        return competitionTerms != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionTermsViewModel that = (CompetitionTermsViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(termsAndConditions, that.termsAndConditions)
                .append(thirdPartyConfig, that.thirdPartyConfig)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(termsAndConditions)
                .append(thirdPartyConfig)
                .toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CompetitionTermsViewModel{").append("\n");
        sb.append("competitionId=").append(competitionId).append("\n");

        if (termsAndConditions != null) {
            sb.append("termsAndConditions{").append("\n");
            sb.append("id=").append(termsAndConditions.getId()).append("\n");
            sb.append("name=").append(termsAndConditions.getName()).append("\n");
            sb.append("template=").append(termsAndConditions.getTemplate()).append("\n");
            sb.append("version=").append(termsAndConditions.getVersion()).append("\n");
            sb.append("}").append("\n");
        }

        if (thirdPartyConfig.getTermsAndConditionsLabel() != null) {
            sb.append("termsAndConditionsLabel=").append(thirdPartyConfig.getTermsAndConditionsLabel()).append("\n");
        }

        if (thirdPartyConfig.getTermsAndConditionsGuidance() != null) {
            sb.append("termsAndConditionsGuidance=").append(thirdPartyConfig.getTermsAndConditionsGuidance()).append("\n");
        }

        sb.append("}");

        return sb.toString();
    }
}