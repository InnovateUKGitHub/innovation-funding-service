package com.worth.ifs.project.resource;

import com.worth.ifs.commons.rest.LocalDateResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class SpendProfileTableResource {

    /*
     * Dynamically holds the months for the duration of the project
     */
    private List<LocalDateResource> months;

    /*
     * Holds the cost per category for each month, the first entry in the list representing the first month and so on.
     */
    private Map<String, List<BigDecimal>> monthlyCostsPerCategoryMap;
    private Map<String, BigDecimal> eligibleCostPerCategoryMap;

    public List<LocalDateResource> getMonths() {
        return months;
    }

    public void setMonths(List<LocalDateResource> months) {
        this.months = months;
    }

    public Map<String, List<BigDecimal>> getMonthlyCostsPerCategoryMap() {
        return monthlyCostsPerCategoryMap;
    }

    public void setMonthlyCostsPerCategoryMap(Map<String, List<BigDecimal>> monthlyCostsPerCategoryMap) {
        this.monthlyCostsPerCategoryMap = monthlyCostsPerCategoryMap;
    }

    public Map<String, BigDecimal> getEligibleCostPerCategoryMap() {
        return eligibleCostPerCategoryMap;
    }

    public void setEligibleCostPerCategoryMap(Map<String, BigDecimal> eligibleCostPerCategoryMap) {
        this.eligibleCostPerCategoryMap = eligibleCostPerCategoryMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SpendProfileTableResource that = (SpendProfileTableResource) o;

        return new EqualsBuilder()
                .append(months, that.months)
                .append(monthlyCostsPerCategoryMap, that.monthlyCostsPerCategoryMap)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(months)
                .toHashCode();
    }
}
