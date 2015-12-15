package com.worth.ifs.application.finance.cost;

import java.math.BigDecimal;

/**
 * {@code SubContractingCost} implements {@link CostItem}
 */
public class SubContractingCost implements CostItem {
    private Long id;
    private BigDecimal cost;
    private String country;
    private String name;
    private String role;

    public SubContractingCost() {
    }

    public SubContractingCost(Long id, BigDecimal cost, String country, String name, String role) {
        this.id = id;
        this.cost = cost;
        this.country = country;
        this.name = name;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    @Override
    public BigDecimal getTotal() {
        return cost;
    }
}
