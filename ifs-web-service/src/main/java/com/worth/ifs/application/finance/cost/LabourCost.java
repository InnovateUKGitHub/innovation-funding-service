package com.worth.ifs.application.finance.cost;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * {@code LabourCost} implements {@link CostItem}
 */
public class LabourCost implements CostItem {
    private final Log log = LogFactory.getLog(getClass());

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

    public BigDecimal getRatePerDay(Integer workingDaysPerYear) {
        if(workingDaysPerYear.equals(0)) {
            return new BigDecimal(0);
        }
        return grossAnnualSalary.divide(new BigDecimal(workingDaysPerYear), 20, RoundingMode.HALF_EVEN);
    }

    public BigDecimal getRate(Integer workingDaysPerYear) {

        if(grossAnnualSalary!=null && workingDaysPerYear != null) {
            rate = getRatePerDay(workingDaysPerYear);
            return rate;
        }
        return rate;
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
            total = new BigDecimal(0);
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
