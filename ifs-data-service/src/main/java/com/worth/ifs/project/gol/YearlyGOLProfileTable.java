package com.worth.ifs.project.gol;

import com.worth.ifs.project.resource.SpendProfileTableResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.Map;

/**
 *
 **/
public class YearlyGOLProfileTable {
    private int year;
    private String yearTotalAmount;
    private Map<Long, BigDecimal> eligibleCostPerOrganisationMap;
    private Map<Long, SpendProfileTableResource> organisationSpendProfiles;
    private BigDecimal totalOfAllEligibleTotals;
    private BigDecimal totalOfAllActualTotals;

    public YearlyGOLProfileTable(final int year, final String yearTotalAmount, Map<Long, BigDecimal> eligibleCostPerOrganisationMap, Map<Long, SpendProfileTableResource> organisationSpendProfiles, BigDecimal totalOfAllEligibleTotals, BigDecimal totalOfAllActualTotals) {
        this.year = year;
        this.yearTotalAmount = yearTotalAmount;
        this.eligibleCostPerOrganisationMap = eligibleCostPerOrganisationMap;
        this.organisationSpendProfiles = organisationSpendProfiles;
        this.totalOfAllEligibleTotals = totalOfAllEligibleTotals;
        this.totalOfAllActualTotals = totalOfAllActualTotals;
    }

    public int getYear() {
        return year;
    }

    public String getYearTotalAmount() {
        return yearTotalAmount;
    }

    @Override public boolean equals(final Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        final YearlyGOLProfileTable that = (YearlyGOLProfileTable) o;

        return new EqualsBuilder()
                .append(year, that.year)
                .append(yearTotalAmount, that.yearTotalAmount)
                .isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(year)
                .append(yearTotalAmount)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("year", year)
                .append("yearTotalAmount", yearTotalAmount)
                .toString();
    }

}
