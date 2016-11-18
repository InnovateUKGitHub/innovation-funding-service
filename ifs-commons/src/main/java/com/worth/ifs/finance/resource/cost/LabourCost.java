package com.worth.ifs.finance.resource.cost;

import com.worth.ifs.finance.resource.category.LabourCostCategory;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.*;
import javax.validation.groups.Default;
import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * {@code LabourCost} implements {@link FinanceRowItem}
 */
public class LabourCost implements FinanceRowItem {

    public interface YearlyWorkingDays {}
    private Long id;

    private String name;

    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    @NotBlank(groups = Default.class, message = NOT_BLANK_MESSAGE)
    private String role;

    @NotNull(groups = Default.class, message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", groups = Default.class, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = MAX_FRACTION, groups = Default.class, message = MAX_DIGITS_MESSAGE)
    private BigDecimal grossAnnualSalary;

    @NotNull
    @Min.List({
            @Min(value=1, groups = Default.class, message = VALUE_MUST_BE_HIGHER_MESSAGE),
            @Min(value=1, groups = LabourCost.YearlyWorkingDays.class, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    })
    @Max(value=365, groups = LabourCost.YearlyWorkingDays.class, message = VALUE_MUST_BE_LOWER_MESSAGE)
    @Digits(integer = MAX_DIGITS_INT, fraction = MAX_FRACTION, message = MAX_DIGITS_MESSAGE)
    private Integer labourDays;
    private BigDecimal rate; // calculated field, no validation
    private String description;
    private BigDecimal total; // calculated field, no validation

    public LabourCost() {

    }

    public LabourCost(Long id, String name, String role, BigDecimal grossAnnualSalary, Integer labourDays, String description) {
        this();
        this.id = id;
        this.name = name;
        this.role = role;
        if(StringUtils.isNotEmpty(this.name)
                && this.name.equals(LabourCostCategory.WORKING_DAYS_KEY)
                && StringUtils.isEmpty(this.role)){
            // User is only allowed to enter the labourDays on this instance, so need to fill the role field for validation.
            this.role =LabourCostCategory.WORKING_DAYS_PER_YEAR;
        }
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

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getMinRows() {
        return 0;
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
    public FinanceRowType getCostType() {
        return FinanceRowType.LABOUR;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
