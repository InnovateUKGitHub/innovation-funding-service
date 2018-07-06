package org.innovateuk.ifs.project.grantofferletter.model;


import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Creates the grant offer letter totals finance table, used by the html renderer for the grant offer letter
 */

@Component
public class GrantOfferLetterFinanceTotalsTable extends GrantOfferLetterFinanceTable {

    private List<String> industrialOrgs;
    private List<String> academicOrgs;
    private Map<String, BigDecimal> grantClaims;
    private Map<String, BigDecimal> totalEligibleCosts;
    private Map<String, BigDecimal> totalGrant;

    public GrantOfferLetterFinanceTotalsTable() {

    }

    public GrantOfferLetterFinanceTotalsTable(Map<String, BigDecimal> grantClaims,
                                              Map<String, BigDecimal> totalEligibleCosts,
                                              Map<String, BigDecimal> totalGrant,
                                              List<String> industrialOrgs,
                                              List<String> academicOrgs) {
        this.grantClaims = grantClaims;
        this.totalEligibleCosts = totalEligibleCosts;
        this.totalGrant = totalGrant;
        this.industrialOrgs = industrialOrgs;
        this.academicOrgs = academicOrgs;

    }

    //    public void populate(Map<String, List<ProjectFinanceRow>> industrialFinances,
//                         Map<String, List<ProjectFinanceRow>> academicFinances) {
//        industrialOrgs = new ArrayList<>(industrialFinances.keySet());
//        academicOrgs = new ArrayList<>(academicFinances.keySet());
//        totalFinances.putAll(industrialFinances);
//        totalFinances.putAll(academicFinances);
//        grantClaims = sumByFinancialType(totalFinances, "grant-claim");
//        totalFinances.forEach((org, finances) -> totalEligibleCosts.put(org,
//                                                                      finances.stream().map(ProjectFinanceRow::getCost).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add)));
//
//        totalFinances.keySet().forEach( org ->
//                                                totalGrant.put(org,
//                                                               totalEligibleCosts.get(org).multiply(grantClaims.get(org)))
//        );
//    }

    public List<String> getIndustrialOrgs() {
        return industrialOrgs;
    }

    public List<String> getAcademicOrgs() {
        return academicOrgs;
    }

    public BigDecimal getGrantClaim(String org) {
        return grantClaims.get(org);
    }

    public BigDecimal getTotalEligibleCosts(String org) {
        return totalEligibleCosts.get(org);
    }

    public BigDecimal getTotalGrant(String org) {
        return totalGrant.get(org);
    }
}
