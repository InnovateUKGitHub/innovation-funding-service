package org.innovateuk.ifs.project.spendprofile.viewmodel;

import org.innovateuk.ifs.commons.rest.LocalDateResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * View model for total spend project spend table. Shows all organisations spend profile totals in one table.
 */
public class TotalProjectSpendProfileTableViewModel {

    private List<LocalDateResource> months;
    private Map<Long, List<BigDecimal>> monthlyCostsPerOrganisationMap;
    private Map<Long, BigDecimal> eligibleCostPerOrganisationMap;
    private Map<Long, BigDecimal> organisationToActualTotal;
    private List<BigDecimal> totalForEachMonth;
    private BigDecimal totalOfAllActualTotals;
    private BigDecimal totalOfAllEligibleTotals;
    private Map<Long, String> organisationNameMap;
    private OrganisationResource leadOrganisation;

    public TotalProjectSpendProfileTableViewModel(List<LocalDateResource> months, Map<Long, List<BigDecimal>> monthlyCostsPerOrganisationMap,
                                                  Map<Long, BigDecimal> eligibleCostPerOrganisationMap, Map<Long, BigDecimal> organisationToActualTotal,
                                                  List<BigDecimal> totalForEachMonth, BigDecimal totalOfAllActualTotals, BigDecimal totalOfAllEligibleTotals,
                                                  Map<Long, String> organisationNameMap, OrganisationResource leadOrganisation) {
        this.months = months;
        this.monthlyCostsPerOrganisationMap = monthlyCostsPerOrganisationMap;
        this.eligibleCostPerOrganisationMap = eligibleCostPerOrganisationMap;
        this.organisationToActualTotal = organisationToActualTotal;
        this.totalForEachMonth = totalForEachMonth;
        this.totalOfAllActualTotals = totalOfAllActualTotals;
        this.totalOfAllEligibleTotals = totalOfAllEligibleTotals;
        this.organisationNameMap = organisationNameMap;
        this.leadOrganisation = leadOrganisation;
    }



    public List<LocalDateResource> getMonths() {
        return months;
    }

    public void setMonths(List<LocalDateResource> months) {
        this.months = months;
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

    public Map<Long, List<BigDecimal>> getMonthlyCostsPerOrganisationMap() {
        return monthlyCostsPerOrganisationMap;
    }

    public Map<Long, BigDecimal> getEligibleCostPerOrganisationMap() {
        return eligibleCostPerOrganisationMap;
    }

    public Map<Long, BigDecimal> getOrganisationToActualTotal() {
        return organisationToActualTotal;
    }

    public Map<Long, String> getOrganisationNameMap() {
        return organisationNameMap;
    }

    public OrganisationResource getLeadOrganisation() { return leadOrganisation; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TotalProjectSpendProfileTableViewModel that = (TotalProjectSpendProfileTableViewModel) o;

        return new EqualsBuilder().append(months, that.months).append(monthlyCostsPerOrganisationMap, that.monthlyCostsPerOrganisationMap).append(eligibleCostPerOrganisationMap, that.eligibleCostPerOrganisationMap).append(organisationToActualTotal, that.organisationToActualTotal).append(totalForEachMonth, that.totalForEachMonth).append(totalOfAllActualTotals, that.totalOfAllActualTotals).append(totalOfAllEligibleTotals, that.totalOfAllEligibleTotals).append(organisationNameMap, that.organisationNameMap).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(months).append(monthlyCostsPerOrganisationMap).append(eligibleCostPerOrganisationMap).append(organisationToActualTotal).append(totalForEachMonth).append(totalOfAllActualTotals).append(totalOfAllEligibleTotals).append(organisationNameMap).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("months", months).append("monthlyCostsPerOrganisationMap", monthlyCostsPerOrganisationMap).append("eligibleCostPerOrganisationMap", eligibleCostPerOrganisationMap).append("organisationToActualTotal", organisationToActualTotal).append("totalForEachMonth", totalForEachMonth).append("totalOfAllActualTotals", totalOfAllActualTotals).append("totalOfAllEligibleTotals", totalOfAllEligibleTotals).append("organisationNameMap", organisationNameMap).toString();
    }
}
