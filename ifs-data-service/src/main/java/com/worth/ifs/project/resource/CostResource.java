package com.worth.ifs.project.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CostResource that = (CostResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(value, that.value)
                .append(costGroup, that.costGroup)
                .append(costTimePeriod, that.costTimePeriod)
                .append(costCategory, that.costCategory)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(value)
                .toHashCode();
    }
}


