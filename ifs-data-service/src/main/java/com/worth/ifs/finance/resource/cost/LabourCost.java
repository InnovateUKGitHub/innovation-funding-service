package com.worth.ifs.finance.resource.cost;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * {@code LabourCost} implements {@link CostItem}
 */
public class LabourCost implements CostItem {
    private Long id;

    private String name;

    @NotBlank
    private String role;

    @DecimalMin(value = "0")
    @Digits(integer = MAX_DIGITS, fraction = 0)
    private BigDecimal grossAnnualSalary;

    @NotNull
    @Min(0)
    private Integer labourDays;
    private BigDecimal rate; // calculated field, no validation
    private String description;
    private BigDecimal total; // calculated field, no validation
    private CostType costType;

    public LabourCost() {
        this.costType = CostType.LABOUR;
    }

    public LabourCost(Long id, String name, String role, BigDecimal grossAnnualSalary, Integer labourDays, String description) {
        this();
        this.id = id;
        this.name = name;
        this.role = role;
        this.grossAnnualSalary = grossAnnualSalary;
        this.labourDays = labourDays;
        this.description = description;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
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

    @Override
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

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setLabourDays(Integer labourDays) {
        this.labourDays = labourDays;
    }

    @Override
    public CostType getCostType() {
        return costType;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
