package com.worth.ifs.finance.resource.cost;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

/**
 * {@code SubContractingCost} implements {@link CostItem}
 */
public class SubContractingCost implements CostItem {
    private Long id;
    @DecimalMin(value = "0")
    @Digits(integer = MAX_DIGITS, fraction = 0)
    private BigDecimal cost;
    private String country;
    private String name;
    private String role;
    private CostType costType;

    public SubContractingCost() {
        this.costType = CostType.SUBCONTRACTING_COSTS;
    }

    public SubContractingCost(Long id, BigDecimal cost, String country, String name, String role) {
        this();
        this.id = id;
        this.cost = cost;
        this.country = country;
        this.name = name;
        this.role = role;
    }

    @Override
    public Long getId() {
        return id;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public String getCountry() {
        return country;
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

    @Override
    public BigDecimal getTotal() {
        return cost;
    }

    @Override
    public CostType getCostType() {
        return costType;
    }
}
