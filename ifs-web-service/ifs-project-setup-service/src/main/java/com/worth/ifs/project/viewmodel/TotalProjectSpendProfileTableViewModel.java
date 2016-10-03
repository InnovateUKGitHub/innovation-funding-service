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

        if (months != null ? !months.equals(that.months) : that.months != null) return false;
        if (monthlyCostsPerOrganisationMap != null ? !monthlyCostsPerOrganisationMap.equals(that.monthlyCostsPerOrganisationMap) : that.monthlyCostsPerOrganisationMap != null)
            return false;
        if (eligibleCostPerOrganisationMap != null ? !eligibleCostPerOrganisationMap.equals(that.eligibleCostPerOrganisationMap) : that.eligibleCostPerOrganisationMap != null)
            return false;
        if (organisationToActualTotal != null ? !organisationToActualTotal.equals(that.organisationToActualTotal) : that.organisationToActualTotal != null)
            return false;
        if (totalForEachMonth != null ? !totalForEachMonth.equals(that.totalForEachMonth) : that.totalForEachMonth != null)
            return false;
        if (totalOfAllActualTotals != null ? !totalOfAllActualTotals.equals(that.totalOfAllActualTotals) : that.totalOfAllActualTotals != null)
            return false;
        return totalOfAllEligibleTotals != null ? totalOfAllEligibleTotals.equals(that.totalOfAllEligibleTotals) : that.totalOfAllEligibleTotals == null;

    }

    @Override
    public int hashCode() {
        int result = months != null ? months.hashCode() : 0;
        result = 31 * result + (monthlyCostsPerOrganisationMap != null ? monthlyCostsPerOrganisationMap.hashCode() : 0);
        result = 31 * result + (eligibleCostPerOrganisationMap != null ? eligibleCostPerOrganisationMap.hashCode() : 0);
        result = 31 * result + (organisationToActualTotal != null ? organisationToActualTotal.hashCode() : 0);
        result = 31 * result + (totalForEachMonth != null ? totalForEachMonth.hashCode() : 0);
        result = 31 * result + (totalOfAllActualTotals != null ? totalOfAllActualTotals.hashCode() : 0);
        result = 31 * result + (totalOfAllEligibleTotals != null ? totalOfAllEligibleTotals.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TotalProjectSpendProfileTableViewModel{" +
                "months=" + months +
                ", monthlyCostsPerOrganisationMap=" + monthlyCostsPerOrganisationMap +
                ", eligibleCostPerOrganisationMap=" + eligibleCostPerOrganisationMap +
                ", organisationToActualTotal=" + organisationToActualTotal +
                ", totalForEachMonth=" + totalForEachMonth +
                ", totalOfAllActualTotals=" + totalOfAllActualTotals +
                ", totalOfAllEligibleTotals=" + totalOfAllEligibleTotals +
                '}';
    }
}