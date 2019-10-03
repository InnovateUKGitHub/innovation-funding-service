package org.innovateuk.ifs.finance.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A class used to capture "Your organisation" information without a growth table
 */
public class OrganisationFinancesWithoutGrowthTableResource {

    private OrganisationSize organisationSize;
    private Long turnover;
    private Long headCount;

    private Boolean stateAidAgreed;

    public OrganisationFinancesWithoutGrowthTableResource(
            OrganisationSize organisationSize,
            Long turnover,
            Long headCount,
            Boolean stateAidAgreed) {

        this.organisationSize = organisationSize;
        this.turnover = turnover;
        this.headCount = headCount;
        this.stateAidAgreed = stateAidAgreed;
    }

    public OrganisationFinancesWithoutGrowthTableResource() {
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public Long getTurnover() {
        return turnover;
    }

    public void setTurnover(Long turnover) {
        this.turnover = turnover;
    }

    public Long getHeadCount() {
        return headCount;
    }

    public void setHeadCount(Long headCount) {
        this.headCount = headCount;
    }

    public Boolean getStateAidAgreed() {
        return stateAidAgreed;
    }

    public void setStateAidAgreed(Boolean stateAidAgreed) {
        this.stateAidAgreed = stateAidAgreed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrganisationFinancesWithoutGrowthTableResource that = (OrganisationFinancesWithoutGrowthTableResource) o;

        return new EqualsBuilder()
                .append(organisationSize, that.organisationSize)
                .append(turnover, that.turnover)
                .append(headCount, that.headCount)
                .append(stateAidAgreed, that.stateAidAgreed)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(organisationSize)
                .append(turnover)
                .append(headCount)
                .append(stateAidAgreed)
                .toHashCode();
    }
}