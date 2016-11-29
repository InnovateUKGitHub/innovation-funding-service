package com.worth.ifs.project.finance.resource;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CostTimePeriodResource {

    private Long id;

    private CostResource cost;

    private Integer offsetAmount;

    private TimeUnit offsetUnit;

    private Integer durationAmount;

    private TimeUnit durationUnit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CostResource getCost() {
        return cost;
    }

    public void setCost(CostResource cost) {
        this.cost = cost;
    }

    public Integer getOffsetAmount() {
        return offsetAmount;
    }

    public void setOffsetAmount(Integer offsetAmount) {
        this.offsetAmount = offsetAmount;
    }

    public TimeUnit getOffsetUnit() {
        return offsetUnit;
    }

    public void setOffsetUnit(TimeUnit offsetUnit) {
        this.offsetUnit = offsetUnit;
    }

    public Integer getDurationAmount() {
        return durationAmount;
    }

    public void setDurationAmount(Integer durationAmount) {
        this.durationAmount = durationAmount;
    }

    public TimeUnit getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(TimeUnit durationUnit) {
        this.durationUnit = durationUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CostTimePeriodResource that = (CostTimePeriodResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(cost, that.cost)
                .append(offsetAmount, that.offsetAmount)
                .append(offsetUnit, that.offsetUnit)
                .append(durationAmount, that.durationAmount)
                .append(durationUnit, that.durationUnit)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(cost)
                .append(offsetAmount)
                .toHashCode();
    }
}
