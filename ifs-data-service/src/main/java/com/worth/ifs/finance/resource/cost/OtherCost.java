package com.worth.ifs.finance.resource.cost;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class OtherCost implements FinanceRowItem {
    private Long id;

    @NotBlank
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String description;

    @NotNull
    @DecimalMin(value = "1")
    @Digits(integer = MAX_DIGITS, fraction = MAX_FRACTION)
    private BigDecimal cost;

    private String name;

    public OtherCost() {
        this.name = getCostType().getType();
    }

    public OtherCost(Long id, String description, BigDecimal cost) {
        this();
        this.id = id;
        this.description = description;
        this.cost = cost;
    }

    @Override
    public Long getId() {
        return id;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public BigDecimal getTotal() {
        return cost;
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.OTHER_COSTS;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getMinRows() {
        return 0;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
