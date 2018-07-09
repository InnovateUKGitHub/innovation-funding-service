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
    private BigDecimal industryTotalEligibleCosts;
    private BigDecimal academicTotalEligibleCosts;
    private BigDecimal allTotalEligibleCosts;
    private BigDecimal industryTotalGrant;
    private BigDecimal academicTotalGrant;
    private BigDecimal allTotalGrant;
    private BigDecimal industryTotalGrantClaim;
    private BigDecimal academicTotalGrantClaim;
    private BigDecimal allTotalGrantClaim;


    public GrantOfferLetterFinanceTotalsTable() {

    }

    public GrantOfferLetterFinanceTotalsTable(Map<String, BigDecimal> grantClaims,
                                              Map<String, BigDecimal> totalEligibleCosts,
                                              Map<String, BigDecimal> totalGrant,
                                              List<String> industrialOrgs,
                                              List<String> academicOrgs,
                                              BigDecimal industryTotalEligibleCosts,
                                              BigDecimal academicTotalEligibleCosts,
                                              BigDecimal allTotalEligibleCosts,
                                              BigDecimal industryTotalGrant,
                                              BigDecimal academicTotalGrant,
                                              BigDecimal allTotalGrant,
                                              BigDecimal industryTotalGrantClaim,
                                              BigDecimal academicTotalGrantClaim,
                                              BigDecimal allTotalGrantClaim) {
        this.grantClaims = grantClaims;
        this.totalEligibleCosts = totalEligibleCosts;
        this.totalGrant = totalGrant;
        this.industrialOrgs = industrialOrgs;
        this.academicOrgs = academicOrgs;
        this.industryTotalEligibleCosts = industryTotalEligibleCosts;
        this.academicTotalEligibleCosts = academicTotalEligibleCosts;
        this.allTotalEligibleCosts = allTotalEligibleCosts;
        this.industryTotalGrant = industryTotalGrant;
        this.academicTotalGrant = academicTotalGrant;
        this.allTotalGrant = allTotalGrant;
        this.industryTotalGrantClaim = industryTotalGrantClaim;
        this.academicTotalGrantClaim = academicTotalGrantClaim;
        this.allTotalGrantClaim = allTotalGrantClaim;
    }

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

    public BigDecimal getIndustryTotalEligibleCosts() {
        return industryTotalEligibleCosts;
    }

    public BigDecimal getAcademicTotalEligibleCosts() {
        return academicTotalEligibleCosts;
    }

    public BigDecimal getAllTotalEligibleCosts() {
        return allTotalEligibleCosts;
    }

    public BigDecimal getIndustryTotalGrant() {
        return industryTotalGrant;
    }

    public BigDecimal getAcademicTotalGrant() {
        return academicTotalGrant;
    }

    public BigDecimal getAllTotalGrant() {
        return allTotalGrant;
    }

    public BigDecimal getIndustryTotalGrantClaim() {
        return industryTotalGrantClaim;
    }

    public BigDecimal getAcademicTotalGrantClaim() {
        return academicTotalGrantClaim;
    }

    public BigDecimal getAllTotalGrantClaim() {
        return allTotalGrantClaim;
    }
}
