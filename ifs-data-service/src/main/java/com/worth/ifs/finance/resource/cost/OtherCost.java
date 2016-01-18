package com.worth.ifs.finance.resource.cost;

import java.math.BigDecimal;

public class OtherCost implements CostItem {
    private Long id;
    private String description;
    private BigDecimal cost;

    public OtherCost() {
    }

    public OtherCost(Long id, String description, BigDecimal cost) {
        this.id = id;
        this.description = description;
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getTotal() {
        return cost;
    }
}
