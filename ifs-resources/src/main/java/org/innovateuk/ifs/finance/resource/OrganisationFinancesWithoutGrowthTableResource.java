package org.innovateuk.ifs.finance.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.math.BigDecimal;

/**
 * A class used to capture "Your organisation" information without a growth table
 */
public class OrganisationFinancesWithoutGrowthTableResource {

    private OrganisationSize organisationSize;
    private BigDecimal turnover;
    private Long headCount;

    public OrganisationFinancesWithoutGrowthTableResource(
            OrganisationSize organisationSize,
            BigDecimal turnover,
            Long headCount) {

        this.organisationSize = organisationSize;
        this.turnover = turnover;
        this.headCount = headCount;
    }

    public OrganisationFinancesWithoutGrowthTableResource() {
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public BigDecimal getTurnover() {
        return turnover;
    }

    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
    }

    public Long getHeadCount() {
        return headCount;
    }

    public void setHeadCount(Long headCount) {
        this.headCount = headCount;
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
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(organisationSize)
                .append(turnover)
                .append(headCount)
                .toHashCode();
    }
}