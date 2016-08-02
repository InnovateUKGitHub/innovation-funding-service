package com.worth.ifs.project.resource;


import java.math.BigDecimal;

public class SpendProfileResource {

    private BigDecimal eligibleLabourCost;
    private BigDecimal eligibleAdminSupportCost;
    private BigDecimal eligibleMaterialCost;
    private BigDecimal eligibleCapitalCost;
    private BigDecimal eligibleSubcontractingCost;
    private BigDecimal eligibleTravelAndSubsistenceCost;
    private BigDecimal eligibleOtherCost;

    public BigDecimal getEligibleLabourCost() {
        return eligibleLabourCost;
    }

    public void setEligibleLabourCost(BigDecimal eligibleLabourCost) {
        this.eligibleLabourCost = eligibleLabourCost;
    }

    public BigDecimal getEligibleAdminSupportCost() {
        return eligibleAdminSupportCost;
    }

    public void setEligibleAdminSupportCost(BigDecimal eligibleAdminSupportCost) {
        this.eligibleAdminSupportCost = eligibleAdminSupportCost;
    }

    public BigDecimal getEligibleMaterialCost() {
        return eligibleMaterialCost;
    }

    public void setEligibleMaterialCost(BigDecimal eligibleMaterialCost) {
        this.eligibleMaterialCost = eligibleMaterialCost;
    }

    public BigDecimal getEligibleCapitalCost() {
        return eligibleCapitalCost;
    }

    public void setEligibleCapitalCost(BigDecimal eligibleCapitalCost) {
        this.eligibleCapitalCost = eligibleCapitalCost;
    }

    public BigDecimal getEligibleSubcontractingCost() {
        return eligibleSubcontractingCost;
    }

    public void setEligibleSubcontractingCost(BigDecimal eligibleSubcontractingCost) {
        this.eligibleSubcontractingCost = eligibleSubcontractingCost;
    }

    public BigDecimal getEligibleTravelAndSubsistenceCost() {
        return eligibleTravelAndSubsistenceCost;
    }

    public void setEligibleTravelAndSubsistenceCost(BigDecimal eligibleTravelAndSubsistenceCost) {
        this.eligibleTravelAndSubsistenceCost = eligibleTravelAndSubsistenceCost;
    }

    public BigDecimal getEligibleOtherCost() {
        return eligibleOtherCost;
    }

    public void setEligibleOtherCost(BigDecimal eligibleOtherCost) {
        this.eligibleOtherCost = eligibleOtherCost;
    }
}
