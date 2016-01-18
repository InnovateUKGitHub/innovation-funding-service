package com.worth.ifs.finance.resource.cost;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * {@code LabourCost} implements {@link CostItem}
 */
public class LabourCost implements CostItem {
    private Long id;
    private String role;
    private BigDecimal grossAnnualSalary;
    private Integer labourDays;
    private BigDecimal rate;
    private String description;
    private BigDecimal total;

    public LabourCost() {
    }

    public LabourCost(Long id, String role, BigDecimal grossAnnualSalary, Integer labourDays, String description) {
        this.id = id;
        this.role = role;
        this.grossAnnualSalary = grossAnnualSalary;
        this.labourDays = labourDays;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public BigDecimal getGrossAnnualSalary() {
        return grossAnnualSalary;
    }

    public BigDecimal getRate(Integer workingDaysPerYear) {
        rate = getRatePerDay(workingDaysPerYear);
        return rate;
    }

    private BigDecimal getRatePerDay(Integer workingDaysPerYear) {
        if(grossAnnualSalary == null || workingDaysPerYear == null) {
            return null;
        }

        if(workingDaysPerYear.equals(0)) {
            return BigDecimal.ZERO;
        }

        return grossAnnualSalary.divide(new BigDecimal(workingDaysPerYear), 5, RoundingMode.HALF_EVEN);
    }

    public BigDecimal getRate() {
        return rate;
    }

    public Integer getLabourDays() {
        return labourDays;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public BigDecimal getTotal(Integer workingDaysPerYear) {
        getRate(workingDaysPerYear);
        calculateTotal();
        return total;
    }

    private void calculateTotal() {
        if(rate!=null && labourDays!=null) {
            total = rate.multiply(new BigDecimal(labourDays));
        } else {
            total = BigDecimal.ZERO;
        }
    }

    public void setGrossAnnualSalary(BigDecimal grossAnnualSalary) {
        this.grossAnnualSalary = grossAnnualSalary;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setLabourDays(Integer labourDays) {
        this.labourDays = labourDays;
    }
}
