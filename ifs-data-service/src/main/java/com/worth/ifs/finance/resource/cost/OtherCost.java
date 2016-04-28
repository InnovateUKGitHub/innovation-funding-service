package com.worth.ifs.finance.resource.cost;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

public class OtherCost implements CostItem {
    private Long id;

    @NotBlank
    private String description;

    @DecimalMin(value = "0")
    @Digits(integer = MAX_DIGITS, fraction = 0)
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
    public CostType getCostType() {
        return CostType.OTHER_COSTS;
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
}
