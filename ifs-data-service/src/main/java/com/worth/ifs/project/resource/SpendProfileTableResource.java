package com.worth.ifs.project.resource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class SpendProfileTableResource {

    /*
     * Dynamically holds the months for the duration of the project
     */
    private List<LocalDate> months;

    /*
     * Holds the cost for each month, the first entry representing the first month and so on
     */
    private List<BigDecimal> monthlyLabourCost;
    private List<BigDecimal> monthlyAdminSupportCost;
    private List<BigDecimal> monthlyMaterialCost;
    private List<BigDecimal> monthlyCapitalCost;
    private List<BigDecimal> monthlySubcontractingCost;
    private List<BigDecimal> monthlyTravelAndSubsistenceCost;
    private List<BigDecimal> monthlyOtherCost;

    public List<LocalDate> getMonths() {
        return months;
    }

    public void setMonths(List<LocalDate> months) {
        this.months = months;
    }

    public List<BigDecimal> getMonthlyLabourCost() {
        return monthlyLabourCost;
    }

    public void setMonthlyLabourCost(List<BigDecimal> monthlyLabourCost) {
        this.monthlyLabourCost = monthlyLabourCost;
    }

    public List<BigDecimal> getMonthlyAdminSupportCost() {
        return monthlyAdminSupportCost;
    }

    public void setMonthlyAdminSupportCost(List<BigDecimal> monthlyAdminSupportCost) {
        this.monthlyAdminSupportCost = monthlyAdminSupportCost;
    }

    public List<BigDecimal> getMonthlyMaterialCost() {
        return monthlyMaterialCost;
    }

    public void setMonthlyMaterialCost(List<BigDecimal> monthlyMaterialCost) {
        this.monthlyMaterialCost = monthlyMaterialCost;
    }

    public List<BigDecimal> getMonthlyCapitalCost() {
        return monthlyCapitalCost;
    }

    public void setMonthlyCapitalCost(List<BigDecimal> monthlyCapitalCost) {
        this.monthlyCapitalCost = monthlyCapitalCost;
    }

    public List<BigDecimal> getMonthlySubcontractingCost() {
        return monthlySubcontractingCost;
    }

    public void setMonthlySubcontractingCost(List<BigDecimal> monthlySubcontractingCost) {
        this.monthlySubcontractingCost = monthlySubcontractingCost;
    }

    public List<BigDecimal> getMonthlyTravelAndSubsistenceCost() {
        return monthlyTravelAndSubsistenceCost;
    }

    public void setMonthlyTravelAndSubsistenceCost(List<BigDecimal> monthlyTravelAndSubsistenceCost) {
        this.monthlyTravelAndSubsistenceCost = monthlyTravelAndSubsistenceCost;
    }

    public List<BigDecimal> getMonthlyOtherCost() {
        return monthlyOtherCost;
    }

    public void setMonthlyOtherCost(List<BigDecimal> monthlyOtherCost) {
        this.monthlyOtherCost = monthlyOtherCost;
    }
}
