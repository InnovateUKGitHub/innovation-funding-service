package org.innovateuk.ifs.project.gol;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 *
 **/
public class YearlyGOLProfileTable {

    private Map<String, Integer> organisationAndGrantPercentageMap;
    private Map<String, List<String>> organisationYearsMap;
    private Map<String, List<BigDecimal>> organisationEligibleCostTotal;
    private Map<String, List<BigDecimal>> organisationGrantAllocationTotal;
    private Map<String, BigDecimal> yearEligibleCostTotal;
    private Map<String, BigDecimal> yearGrantAllocationTotal;
    private BigDecimal eligibleCostGrandTotal;
    private BigDecimal grantAllocationGrandTotal;

    public YearlyGOLProfileTable(Map<String, Integer> organisationAndGrantPercentageMap,
                                 Map<String, List<String>> organisationYearsMap,
                                 Map<String, List<BigDecimal>> organisationEligibleCostTotal,
                                 Map<String, List<BigDecimal>> organisationGrantAllocationTotal,
                                 Map<String, BigDecimal> yearEligibleCostTotal,
                                 Map<String, BigDecimal> yearGrantAllocationTotal,
                                 BigDecimal eligibleCostGrandTotal,
                                 BigDecimal grantAllocationGrandTotal) {
        this.organisationAndGrantPercentageMap = organisationAndGrantPercentageMap;
        this.organisationYearsMap = organisationYearsMap;
        this.organisationEligibleCostTotal = organisationEligibleCostTotal;
        this.organisationGrantAllocationTotal = organisationGrantAllocationTotal;
        this.yearEligibleCostTotal = yearEligibleCostTotal;
        this.yearGrantAllocationTotal = yearGrantAllocationTotal;
        this.eligibleCostGrandTotal = eligibleCostGrandTotal;
        this.grantAllocationGrandTotal = grantAllocationGrandTotal;
    }

    public int getNumberOfColHeader() {
        return yearEligibleCostTotal.size() + 1;
    }

    public int getNumberOfCols() {
        return (getNumberOfColHeader() + 1) * 2;
    }

    public Map<String, Integer> getOrganisationAndGrantPercentageMap() {
        return organisationAndGrantPercentageMap;
    }

    public Map<String, List<String>> getOrganisationYearsMap() {
        return organisationYearsMap;
    }

    public Map<String, List<BigDecimal>> getOrganisationEligibleCostTotal() {
        return organisationEligibleCostTotal;
    }

    public Map<String, List<BigDecimal>> getOrganisationGrantAllocationTotal() {
        return organisationGrantAllocationTotal;
    }

    public Map<String, BigDecimal> getYearEligibleCostTotal() {
        return yearEligibleCostTotal;
    }

    public Map<String, BigDecimal> getYearGrantAllocationTotal() {
        return yearGrantAllocationTotal;
    }

    public BigDecimal getEligibleCostGrandTotal() {
        return eligibleCostGrandTotal;
    }

    public BigDecimal getGrantAllocationGrandTotal() {
        return grantAllocationGrandTotal;
    }
}
