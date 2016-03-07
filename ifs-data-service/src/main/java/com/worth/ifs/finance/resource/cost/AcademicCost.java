package com.worth.ifs.finance.resource.cost;

import com.worth.ifs.organisation.domain.Academic;

import java.math.BigDecimal;

public class AcademicCost implements CostItem {
    private Long id;
    private String name;
    private CostType costType;
    private BigDecimal cost;
    private String item;

    public AcademicCost() {

    }

    public AcademicCost(Long id, String name, BigDecimal cost, String item) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.item = item;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public BigDecimal getTotal() {
        return cost;
    }

    @Override
    public CostType getCostType() {
        return costType;
    }

    public String getItem() {
        return item;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getCost() {
        return cost;
    }
}
