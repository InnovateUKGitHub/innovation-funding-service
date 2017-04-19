package org.innovateuk.ifs.project.gol;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapValue;

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
    private Map<Long, String> organisationName;

    public YearlyGOLProfileTable(Map<String, Integer> organisationAndGrantPercentageMap,
                                 Map<String, List<String>> organisationYearsMap,
                                 Map<String, List<BigDecimal>> organisationEligibleCostTotal,
                                 Map<String, List<BigDecimal>> organisationGrantAllocationTotal,
                                 Map<String, BigDecimal> yearEligibleCostTotal,
                                 Map<String, BigDecimal> yearGrantAllocationTotal,
                                 Map<Long, String> organisationIdToName) {
        this.organisationAndGrantPercentageMap = organisationAndGrantPercentageMap;
        this.organisationYearsMap = organisationYearsMap;
        this.organisationEligibleCostTotal = organisationEligibleCostTotal;
        this.organisationGrantAllocationTotal = organisationGrantAllocationTotal;
        this.yearEligibleCostTotal = yearEligibleCostTotal;
        this.yearGrantAllocationTotal = yearGrantAllocationTotal;
        this.organisationName = organisationIdToName;
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


    public BigDecimal getGrantAllocationGrandTotal() {
        return yearGrantAllocationTotal.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getEligibleCostGrandTotal() {
        return yearEligibleCostTotal.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, BigDecimal> getEligibleCostGrandTotalPerOrganisation() {
        return simpleMapValue(organisationEligibleCostTotal, costs -> {
            return costs
                    .stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        });
    }
    public Map<String, BigDecimal> getGrantAllocationGrandTotalPerOrganisation() {
        return simpleMapValue(organisationGrantAllocationTotal, costs -> {
            return costs
                    .stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        });
    }

    public Map<Long, String> getOrganisationName() {
        return organisationName;
    }

}
