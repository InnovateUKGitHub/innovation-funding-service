package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Resource representing the finance part of competition setup
 */
public class CompetitionSetupFinanceResource {
    private Long competitionId;
    private boolean fullApplicationFinance;
    private boolean includeGrowthTable;

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public boolean isFullApplicationFinance() {
        return fullApplicationFinance;
    }

    public void setFullApplicationFinance(boolean fullApplicationFinance) {
        this.fullApplicationFinance = fullApplicationFinance;
    }

    public boolean isIncludeGrowthTable() {
        return includeGrowthTable;
    }

    public void setIncludeGrowthTable(boolean includeGrowthTable) {
        this.includeGrowthTable = includeGrowthTable;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        CompetitionSetupFinanceResource that = (CompetitionSetupFinanceResource) o;

        return new EqualsBuilder()
                .append(fullApplicationFinance, that.fullApplicationFinance)
                .append(includeGrowthTable, that.includeGrowthTable)
                .append(competitionId, that.competitionId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(fullApplicationFinance)
                .append(includeGrowthTable)
                .toHashCode();
    }


    @Override
    public String toString() {
        return "CompetitionSetupFinanceResource{" +
                "competitionId=" + competitionId +
                ", fullApplicationFinance=" + fullApplicationFinance +
                ", includeGrowthTable=" + includeGrowthTable +
                '}';
    }
}
