package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Resource representing the finance part of competition setup
 */
public class CompetitionSetupFinanceResource {
    private Long competitionId;
    private ApplicationFinanceType applicationFinanceType;
    private Boolean includeGrowthTable;
    private Boolean includeYourOrganisationSection;
    private Boolean includeJesForm;
    private Boolean includePaymentMilestone;

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public ApplicationFinanceType getApplicationFinanceType() {
        return applicationFinanceType;
    }

    public void setApplicationFinanceType(final ApplicationFinanceType applicationFinanceType) {
        this.applicationFinanceType = applicationFinanceType;
    }

    public Boolean getIncludeGrowthTable() {
        return includeGrowthTable;
    }

    public void setIncludeGrowthTable(Boolean includeGrowthTable) {
        this.includeGrowthTable = includeGrowthTable;
    }

    public Boolean getIncludeYourOrganisationSection() {
        return includeYourOrganisationSection;
    }

    public void setIncludeYourOrganisationSection(final Boolean includeYourOrganisationSection) {
        this.includeYourOrganisationSection = includeYourOrganisationSection;
    }

    public Boolean getIncludeJesForm() {
        return includeJesForm;
    }

    public void setIncludeJesForm(Boolean includeJesForm) {
        this.includeJesForm = includeJesForm;
    }

    public Boolean getIncludePaymentMilestone() {
        return includePaymentMilestone;
    }

    public void setIncludePaymentMilestone(Boolean includePaymentMilestone) {
        this.includePaymentMilestone = includePaymentMilestone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionSetupFinanceResource that = (CompetitionSetupFinanceResource) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(applicationFinanceType, that.applicationFinanceType)
                .append(includeGrowthTable, that.includeGrowthTable)
                .append(includeYourOrganisationSection, that.includeYourOrganisationSection)
                .append(includeJesForm, that.includeJesForm)
                .append(includePaymentMilestone, that.includePaymentMilestone)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(applicationFinanceType)
                .append(includeGrowthTable)
                .append(includeYourOrganisationSection)
                .append(includeJesForm)
                .append(includePaymentMilestone)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "CompetitionSetupFinanceResource{" +
                "competitionId=" + competitionId +
                ", applicationFinanceType=" + applicationFinanceType +
                ", includeGrowthTable=" + includeGrowthTable +
                ", includeYourOrganisationSection=" + includeYourOrganisationSection +
                ", includeJesForm=" + includeJesForm +
                ", includePaymentMilestone=" + includePaymentMilestone +
                '}';
    }
}
