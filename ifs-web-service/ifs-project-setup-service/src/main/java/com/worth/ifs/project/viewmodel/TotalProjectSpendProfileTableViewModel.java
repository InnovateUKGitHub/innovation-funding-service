package com.worth.ifs.project.viewmodel;

import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.validation.ObjectError;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * View model for total spend project spend table. Shows all organisations spend profile totals in one table.
 */
public class TotalProjectSpendProfileTableViewModel {

    /*
    * Dynamically holds the months for the duration of the project
    */
    private List<LocalDateResource> months;

    /*
     * Holds the cost per organisation for each month, the first entry in the list representing the first month and so on.
     */
    private Map<String, List<BigDecimal>> monthlyCostsPerOrganisationMap;
    private Map<String, BigDecimal> eligibleCostPerOrganisationMap;
    private Map<String, BigDecimal> organisationToActualTotal;
    private List<BigDecimal> totalForEachMonth;
    private BigDecimal totalOfAllActualTotals;
    private BigDecimal totalOfAllEligibleTotals;

    public TotalProjectSpendProfileTableViewModel(List<LocalDateResource> months, Map<String, List<BigDecimal>> monthlyCostsPerOrganisationMap, Map<String, BigDecimal> eligibleCostPerOrganisationMap, Map<String, BigDecimal> organisationToActualTotal, List<BigDecimal> totalForEachMonth, BigDecimal totalOfAllActualTotals, BigDecimal totalOfAllEligibleTotals) {
        this.months = months;
        this.monthlyCostsPerOrganisationMap = monthlyCostsPerOrganisationMap;
        this.eligibleCostPerOrganisationMap = eligibleCostPerOrganisationMap;
        this.organisationToActualTotal = organisationToActualTotal;
        this.totalForEachMonth = totalForEachMonth;
        this.totalOfAllActualTotals = totalOfAllActualTotals;
        this.totalOfAllEligibleTotals = totalOfAllEligibleTotals;
    }

    public List<LocalDateResource> getMonths() {
        return months;
    }

    public void setMonths(List<LocalDateResource> months) {
        this.months = months;
    }

    public Map<String, List<BigDecimal>> getMonthlyCostsPerOrganisationMap() {
        return monthlyCostsPerOrganisationMap;
    }

    public Map<String, BigDecimal> getEligibleCostPerOrganisationMap() {
        return eligibleCostPerOrganisationMap;
    }

    public Map<String, BigDecimal> getOrganisationToActualTotal() {
        return organisationToActualTotal;
    }

    public List<BigDecimal> getTotalForEachMonth() {
        return totalForEachMonth;
    }

    public BigDecimal getTotalOfAllActualTotals() {
        return totalOfAllActualTotals;
    }

    public BigDecimal getTotalOfAllEligibleTotals() {
        return totalOfAllEligibleTotals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TotalProjectSpendProfileTableViewModel that = (TotalProjectSpendProfileTableViewModel) o;

        return new EqualsBuilder()
                .append(months, that.months)
                .append(monthlyCostsPerOrganisationMap, that.monthlyCostsPerOrganisationMap)
                .append(eligibleCostPerOrganisationMap, that.eligibleCostPerOrganisationMap)
                .append(organisationToActualTotal, that.organisationToActualTotal)
                .append(totalForEachMonth, that.totalForEachMonth)
                .append(totalOfAllActualTotals, that.totalOfAllActualTotals)
                .append(totalOfAllEligibleTotals, that.totalOfAllEligibleTotals)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(months)
                .append(monthlyCostsPerOrganisationMap)
                .append(eligibleCostPerOrganisationMap)
                .append(organisationToActualTotal)
                .append(totalForEachMonth)
                .append(totalOfAllActualTotals)
                .append(totalOfAllEligibleTotals)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("months", months)
                .append("monthlyCostsPerOrganisationMap", monthlyCostsPerOrganisationMap)
                .append("eligibleCostPerOrganisationMap", eligibleCostPerOrganisationMap)
                .append("organisationToActualTotal", organisationToActualTotal)
                .append("totalForEachMonth", totalForEachMonth)
                .append("totalOfAllActualTotals", totalOfAllActualTotals)
                .append("totalOfAllEligibleTotals", totalOfAllEligibleTotals)
                .toString();
    }
}