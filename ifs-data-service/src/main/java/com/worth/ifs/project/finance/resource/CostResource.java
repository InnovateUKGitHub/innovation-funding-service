package com.worth.ifs.project.finance.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

public class CostResource {

    private Long id;

    private BigDecimal value;

    private CostGroupResource costGroup;

    private CostTimePeriodResource costTimePeriod;

    private CostCategoryResource costCategory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @JsonIgnore
    public CostGroupResource getCostGroup() {
        return costGroup;
    }

    public void setCostGroup(CostGroupResource costGroup) {
        this.costGroup = costGroup;
    }

    public CostTimePeriodResource getCostTimePeriod() {
        return costTimePeriod;
    }

    public void setCostTimePeriod(CostTimePeriodResource costTimePeriod) {
        this.costTimePeriod = costTimePeriod;
    }

    public CostCategoryResource getCostCategory() {
        return costCategory;
    }

    public void setCostCategory(CostCategoryResource costCategory) {
        this.costCategory = costCategory;
    }
}


