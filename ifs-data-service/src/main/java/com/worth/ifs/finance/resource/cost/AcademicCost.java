package com.worth.ifs.finance.resource.cost;

import com.worth.ifs.organisation.domain.Academic;

import java.math.BigDecimal;

public class AcademicCost implements CostItem {
    private Long id;
    private CostType costType;
    private BigDecimal cost;
    private String description;

    public AcademicCost() {

    }

    public AcademicCost(Long id, BigDecimal cost, String description) {
        this.id = id;
        this.cost = cost;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

}
