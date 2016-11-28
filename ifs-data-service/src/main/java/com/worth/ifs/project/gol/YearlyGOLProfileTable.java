package com.worth.ifs.project.gol;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 *
 **/
public class YearlyGOLProfileTable {

    private Map<String, Integer> organisationAndGrantPercentageMap;
    private Map<String, List<String>> organisationYearsMap;
    private Map<String, BigDecimal> organisationEligibleCostTotal;
    private Map<String, BigDecimal> organisationGrantAllocationTotal;
    private Map<String, BigDecimal> yearEligibleCostTotal;
    private Map<String, BigDecimal> yearGrantAllocationTotal;
    private Map<Long, BigDecimal> eligibleCostPerOrganisationMap;

    public YearlyGOLProfileTable(Map<String, Integer> organisationAndGrantPercentageMap,
                                 Map<String, List<String>> organisationYearsMap,
                                 Map<String, BigDecimal> organisationEligibleCostTotal,
                                 Map<String, BigDecimal> organisationGrantAllocationTotal,
                                 Map<String, BigDecimal> yearEligibleCostTotal,
                                 Map<String, BigDecimal> yearGrantAllocationTotal,
                                 Map<Long, BigDecimal> eligibleCostPerOrganisationMap) {
        this.organisationAndGrantPercentageMap = organisationAndGrantPercentageMap;
        this.organisationYearsMap = organisationYearsMap;
        this.organisationEligibleCostTotal = organisationEligibleCostTotal;
        this.organisationGrantAllocationTotal = organisationGrantAllocationTotal;
        this.yearEligibleCostTotal = yearEligibleCostTotal;
        this.yearGrantAllocationTotal = yearGrantAllocationTotal;
        this.eligibleCostPerOrganisationMap = eligibleCostPerOrganisationMap;
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

    public Map<String, BigDecimal> getOrganisationEligibleCostTotal() {
        return organisationEligibleCostTotal;
    }

    public Map<String, BigDecimal> getOrganisationGrantAllocationTotal() {
        return organisationGrantAllocationTotal;
    }

    public Map<String, BigDecimal> getYearEligibleCostTotal() {
        return yearEligibleCostTotal;
    }

    public Map<String, BigDecimal> getYearGrantAllocationTotal() {
        return yearGrantAllocationTotal;
    }

    public Map<Long, BigDecimal> getEligibleCostPerOrganisationMap() {
        return eligibleCostPerOrganisationMap;
    }
}
